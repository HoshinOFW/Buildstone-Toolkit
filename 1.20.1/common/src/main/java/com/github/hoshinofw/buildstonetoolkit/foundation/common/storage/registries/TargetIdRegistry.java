package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/** Persistent {@code pos ↔ targetId} map with refcount-based recycling. See
 *  {@code ProxyBlockEntity} */
public class TargetIdRegistry extends SavedData {
    private static final String NBTNextIdKey = "nextId";
    private static final String NBTRecycleSetKey = "recycleSetArray";
    private static final String NBTForwardKeysKey = "forwardKeys";
    private static final String NBTForwardValuesKey = "forwardValues";
    private static final String NBTRefcountKeysKey = "refcountKeys";
    private static final String NBTRefcountValuesKey = "refcountValues";
    private static final int RECYCLE_SET_WARN_THRESHOLD = 10_000;

    public static final long NO_TARGET_ID = BlockPos.asLong(-1, -1_000_000, -1);

    private final Long2LongOpenHashMap forward = new Long2LongOpenHashMap();
    private final Long2LongOpenHashMap reverse = new Long2LongOpenHashMap();
    // refcount uses fastutil's natural 0L default — a missing entry means "no references"
    // (implicit-0 semantic), no sentinel needed.
    private final Long2LongOpenHashMap refcount = new Long2LongOpenHashMap();
    private final LongOpenHashSet recycleSet = new LongOpenHashSet();
    private long nextId = 0;
    private boolean recycleSetWarningFired = false;

    public TargetIdRegistry() {
        forward.defaultReturnValue(NO_TARGET_ID);
        reverse.defaultReturnValue(NO_TARGET_ID);
    }

    private TargetIdRegistry(long nextId) {
        this();
        this.nextId = nextId;
        setDirty();
    }

    public static TargetIdRegistry build() {
        return new TargetIdRegistry();
    }

    public static TargetIdRegistry build(long nextId) {
        return new TargetIdRegistry(nextId);
    }

    public static boolean isValidTargetId(long targetId) {
        return targetId >= 0L;
    }

    public int size() {
        return forward.size();
    }

    /** Get-or-allocate the targetId for {@code pos}. Does NOT touch refcount —
     *  use {@link #acquire} to claim a logical reference. */
    public long internPos(long pos) {
        long existing = reverse.get(pos);
        if (existing != NO_TARGET_ID) return existing;

        long id = allocateId();
        forward.put(id, pos);
        reverse.put(pos, id);
        return id;
    }

    /** Claim a logical reference to the targetId at {@code pos} (allocating if needed).
     *  Pairs with {@link #release}; every acquire must be balanced by exactly one release. */
    public long acquire(long pos) {
        long id = internPos(pos);
        refcount.addTo(id, 1L);
        setDirty();
        return id;
    }

    /** Drop a reference; recycle when refcount reaches ≤ 0 (covers both the last-release and
     *  internPos-without-acquire cleanup paths). No-op when invalid or already recycled. */
    public void release(long targetId) {
        if (!isValidTargetId(targetId)) return;
        if (!forward.containsKey(targetId)) return;
        long oldValue = refcount.addTo(targetId, -1L);
        setDirty();
        if (oldValue - 1L <= 0L) {
            recycleInternal(targetId);
        }
    }

    /** Refcount for {@code targetId}; 0 for absent. Debug/test. */
    public long getRefcount(long targetId) {
        return refcount.get(targetId);
    }

    public Optional<Long> lookup(long targetId) {
        if (!forward.containsKey(targetId)) return Optional.empty();
        return Optional.of(forward.get(targetId));
    }

    public long reverseLookup(long pos) {
        return reverse.get(pos);
    }

    /** Reroute the targetId at {@code oldPos} to point at {@code newPos} (Sable bulk-rename hook).
     *  Refcount untouched; no-op when nothing maps to {@code oldPos}. */
    public void retarget(long oldPos, long newPos) {
        if (oldPos == newPos) return;
        long id = reverse.get(oldPos);
        if (id == NO_TARGET_ID) return;

        long preExisting = reverse.get(newPos);
        if (preExisting != NO_TARGET_ID && preExisting != id) {
            // Drop the prior occupant's forward entry so the table stays consistent — the
            // proxy holding that targetId will hit the dangling-lookup branch on next load.
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry.retarget: newPos {} already owned by targetId {}; " +
                    "overwriting with {} and orphaning the prior id.",
                    newPos, preExisting, id);
            forward.remove(preExisting);
        }

        forward.put(id, newPos);
        reverse.remove(oldPos);
        reverse.put(newPos, id);
        setDirty();
    }

    public void clear() {
        forward.clear();
        reverse.clear();
        refcount.clear();
        recycleSet.clear();
        nextId = 0;
        setDirty();
    }

    private long allocateId() {
        setDirty();

        while (recycleSet.contains(nextId - 1)) {
            recycleSet.remove(nextId - 1);
            nextId--;
        }

        if (!recycleSet.isEmpty()) {
            LongIterator it = recycleSet.iterator();
            long i = it.nextLong();
            it.remove();
            return i;
        }
        return nextId++;
    }

    private void recycleInternal(long targetId) {
        if (!isValidTargetId(targetId)) return;
        if (!forward.containsKey(targetId)) return;
        long pos = forward.get(targetId);
        forward.remove(targetId);
        if (reverse.get(pos) == targetId) {
            reverse.remove(pos);
        }
        refcount.remove(targetId);
        recycleId(targetId);
    }

    /** Reclaim orphan ids: forward keys with no refcount (legacy internPos-only state)
     *  and refcount keys with no forward (retarget-conflict drift). */
    private void sweepOrphans() {
        LongArrayList orphanForward = new LongArrayList();
        for (Long2LongMap.Entry e : forward.long2LongEntrySet()) {
            if (!refcount.containsKey(e.getLongKey())) orphanForward.add(e.getLongKey());
        }
        for (long id : orphanForward) {
            long pos = forward.get(id);
            forward.remove(id);
            if (reverse.get(pos) == id) reverse.remove(pos);
            recycleId(id);
        }

        LongArrayList orphanRefcount = new LongArrayList();
        for (Long2LongMap.Entry e : refcount.long2LongEntrySet()) {
            if (!forward.containsKey(e.getLongKey())) orphanRefcount.add(e.getLongKey());
        }
        for (long id : orphanRefcount) {
            refcount.remove(id);
            recycleId(id);
        }

        if (!orphanForward.isEmpty() || !orphanRefcount.isEmpty()) {
            BuildstoneToolkit.LOGGER.info(
                    "TargetIdRegistry sweep: reclaimed {} orphan forward entries, {} orphan refcount entries.",
                    orphanForward.size(), orphanRefcount.size());
        }
    }

    private void recycleId(long id) {
        recycleSet.add(id);
        setDirty();
        if (!recycleSetWarningFired && recycleSet.size() >= RECYCLE_SET_WARN_THRESHOLD) {
            recycleSetWarningFired = true;
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry recycleSet has grown to {} entries. This is unexpectedly " +
                    "high and may indicate id-allocation fragmentation or a leak in the recycle " +
                    "path. This warning fires once per server session.",
                    recycleSet.size());
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.putLong(NBTNextIdKey, nextId);
        tag.putLongArray(NBTRecycleSetKey, recycleSet.toLongArray());

        long[] keys = new long[forward.size()];
        long[] values = new long[forward.size()];
        int i = 0;
        for (Long2LongMap.Entry e : forward.long2LongEntrySet()) {
            keys[i] = e.getLongKey();
            values[i] = e.getLongValue();
            i++;
        }
        tag.putLongArray(NBTForwardKeysKey, keys);
        tag.putLongArray(NBTForwardValuesKey, values);

        long[] refcountKeys = new long[refcount.size()];
        long[] refcountValues = new long[refcount.size()];
        int j = 0;
        for (Long2LongMap.Entry e : refcount.long2LongEntrySet()) {
            refcountKeys[j] = e.getLongKey();
            refcountValues[j] = e.getLongValue();
            j++;
        }
        tag.putLongArray(NBTRefcountKeysKey, refcountKeys);
        tag.putLongArray(NBTRefcountValuesKey, refcountValues);
        return tag;
    }

    public static TargetIdRegistry load(CompoundTag nbt) {
        TargetIdRegistry registry = new TargetIdRegistry(nbt.getLong(NBTNextIdKey));
        registry.recycleSet.addAll(LongArrayList.wrap(nbt.getLongArray(NBTRecycleSetKey)));

        long[] keys = nbt.getLongArray(NBTForwardKeysKey);
        long[] values = nbt.getLongArray(NBTForwardValuesKey);
        if (keys.length != values.length) {
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry.load: forward map key/value arrays disagree in length " +
                    "({} vs {}); discarding forward map.",
                    keys.length, values.length);
        } else {
            for (int i = 0; i < keys.length; i++) {
                registry.forward.put(keys[i], values[i]);
                registry.reverse.put(values[i], keys[i]);
            }
        }

        long[] refcountKeys = nbt.getLongArray(NBTRefcountKeysKey);
        long[] refcountValues = nbt.getLongArray(NBTRefcountValuesKey);
        if (refcountKeys.length != refcountValues.length) {
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry.load: refcount map key/value arrays disagree in length " +
                    "({} vs {}); discarding refcount map.",
                    refcountKeys.length, refcountValues.length);
        } else {
            for (int i = 0; i < refcountKeys.length; i++) {
                registry.refcount.put(refcountKeys[i], refcountValues[i]);
            }
        }

        registry.sweepOrphans();
        return registry;
    }
}
