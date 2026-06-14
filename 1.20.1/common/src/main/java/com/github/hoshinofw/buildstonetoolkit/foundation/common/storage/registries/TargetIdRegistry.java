package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;


/** Persistent {@code pos ↔ targetId} map with refcount-based recycling. See
 *  {@code ProxyBlockEntity} */
public class TargetIdRegistry extends SavedData {
    private static final String NBTNextIdKey = "nextId";
    private static final String NBTRecycleSetKey = "recycleSetArray";
    private static final String NBTForwardKeysKey = "forwardKeys";
    private static final String NBTForwardValuesKey = "forwardValues";
    private static final String NBTRefcountKeysKey = "refcountKeys";
    private static final String NBTRefcountValuesKey = "refcountValues";
    private static final String NBTRotationKeysKey = "rotationKeys";
    private static final String NBTRotationValuesKey = "rotationValues";
    private static final String NBTMigrationKeysKey = "migrationKeys";
    private static final String NBTMigrationValuesKey = "migrationValues";
    private static final String NBTMigrationRotKey = "migrationRotValues";
    private static final int RECYCLE_SET_WARN_THRESHOLD = 10_000;
    private static final int MIGRATION_WARN_THRESHOLD = 10_000;

    public static final long NO_TARGET_ID = BlockPos.asLong(-1, -1_000_000, -1);
    public static final long NULL_POS = BlockPos.asLong(30_000_000, 2047, 30_000_000);

    private final Long2LongOpenHashMap forward = new Long2LongOpenHashMap();
    private final Long2LongOpenHashMap reverse = new Long2LongOpenHashMap();

    private final Long2LongOpenHashMap refcount = new Long2LongOpenHashMap();
    private final Long2ByteOpenHashMap rotation = new Long2ByteOpenHashMap();
    private final Long2LongOpenHashMap migration = new Long2LongOpenHashMap();
    private final Long2ByteOpenHashMap migrationRot = new Long2ByteOpenHashMap();
    private final LongOpenHashSet recycleSet = new LongOpenHashSet();
    private long nextId = 0;
    private boolean recycleSetWarningFired = false;

    public TargetIdRegistry() {
        forward.defaultReturnValue(NO_TARGET_ID);
        reverse.defaultReturnValue(NO_TARGET_ID);
        rotation.defaultReturnValue(Rotation3D.IDENTITY_BYTE);
        migration.defaultReturnValue(NO_TARGET_ID);
        migrationRot.defaultReturnValue(Rotation3D.IDENTITY_BYTE);
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

    /** Get or allocate id without touching refcount */
    public long internPos(long pos) {
        if (pos == NULL_POS) return NO_TARGET_ID;
        long existing = reverse.get(pos);
        if (existing != NO_TARGET_ID) return existing;

        long id = allocateId();
        forward.put(id, pos);
        reverse.put(pos, id);
        return id;
    }

    /** Claim a reference, increases refcount. pairs with {@link #release} */
    public long acquire(long pos) {
        long id = internPos(pos);
        refcount.addTo(id, 1L);
        setDirty();
        return id;
    }

    /** Drop a reference, decreases refcount. Pairs with {@link #acquire} */
    public void release(long targetId) {
        if (!isValidTargetId(targetId)) return;
        long terminal = resolveMigration(targetId);
        if (!forward.containsKey(terminal)) return;
        long oldValue = refcount.addTo(terminal, -1L);
        setDirty();
        if (oldValue - 1L <= 0L) {
            recycleInternal(terminal);
            dropMigrationsTo(terminal);
        }
    }

    /** Refcount for {@code targetId}.*/
    public long getRefcount(long targetId) {
        return refcount.get(resolveMigration(targetId));
    }

    /** Terminal id for {@code id} after following any migration. O(1) — the map is flat (§11). */
    public long resolveMigration(long id) {
        long t = migration.get(id);
        return t == NO_TARGET_ID ? id : t;
    }

    /** Edge-delta rotation (§11.1) carrying a migrated id's frame onto its terminal; IDENTITY if none. */
    public byte migrationRotation(long id) {
        return migrationRot.get(id);
    }

    public OptionalLong lookup(long targetId) {
        long pos = forward.get(resolveMigration(targetId));
        if (pos == NO_TARGET_ID) return OptionalLong.empty();
        return OptionalLong.of(pos);
    }

    public long reverseLookup(long pos) {
        return reverse.get(pos);
    }

    /** Cumulative face rotation applied to {@code targetId}'s target; IDENTITY when absent. */
    public Rotation3D getRotation(long targetId) {
        return Rotation3D.fromByte(rotation.get(resolveMigration(targetId)));
    }

    /** Accumulate one rotation event onto {@code targetId}; drops back to absent when it folds to IDENTITY. */
    public void addRotation(long targetId, Rotation3D event) {
        if (!isValidTargetId(targetId)) return;
        targetId = resolveMigration(targetId);
        if (!forward.containsKey(targetId)) return;
        byte next = Rotation3D.compose(rotation.get(targetId), event);
        if (next == Rotation3D.IDENTITY_BYTE) rotation.remove(targetId);
        else rotation.put(targetId, next);
        setDirty();
    }

    /** Reroute the targetId from {@code oldPos} to {@code newPos}. Returns the id pointing to {@code newPos}. Caller must handle the case where the argument and the return mismatch*/
    public long retarget(long oldPos, long newPos) {
        if (oldPos == newPos) return reverse.get(oldPos);
        long id = reverse.get(oldPos);
        if (id == NO_TARGET_ID) return NO_TARGET_ID;

        long preExisting = reverse.get(newPos);
        if (preExisting != NO_TARGET_ID && preExisting != id) {
            migrateOnto(id, preExisting);
            setDirty();
            return preExisting;
        }

        forward.put(id, newPos);
        reverse.remove(oldPos);
        reverse.put(newPos, id);
        setDirty();
        return id;
    }

    /** Point {@code targetId} at a null position. Frees old position. */
    public void park(long targetId) {
        if (!isValidTargetId(targetId) || !forward.containsKey(targetId)) return;
        long oldPos = forward.get(targetId);
        if (reverse.get(oldPos) == targetId) reverse.remove(oldPos);
        forward.put(targetId, NULL_POS);
        setDirty();
    }

    /** Revive a parked {@code targetId} at {@code newPos}. Returns the id owning {@code newPos}, caller must handle the case where the argument and the return mismatch*/
    public long unpark(long targetId, long newPos) {
        if (!isValidTargetId(targetId) || !forward.containsKey(targetId)) return NO_TARGET_ID;
        long preExisting = reverse.get(newPos);
        if (preExisting != NO_TARGET_ID && preExisting != targetId) {
            migrateOnto(targetId, preExisting);
            setDirty();
            return preExisting;
        }
        forward.put(targetId, newPos);
        reverse.put(newPos, targetId);
        setDirty();
        return targetId;
    }

    /** Fold {@code fromId} onto {@code survivor} and merge refcounts*/
    private void migrateOnto(long fromId, long survivor) {
        long n = refcount.get(fromId);
        if (n != 0L) refcount.addTo(survivor, n);
        refcount.remove(fromId);

        long fromPos = forward.get(fromId);
        forward.remove(fromId);
        if (fromPos != NO_TARGET_ID && reverse.get(fromPos) == fromId) reverse.remove(fromPos);

        byte dPrime = Rotation3D.compose(rotation.get(fromId), Rotation3D.inverse(rotation.get(survivor)));
        rotation.remove(fromId);

        migration.put(fromId, survivor);
        if (dPrime != Rotation3D.IDENTITY_BYTE) migrationRot.put(fromId, dPrime);
        else migrationRot.remove(fromId);


        for (Long2LongMap.Entry e : migration.long2LongEntrySet()) {
            if (e.getLongValue() != fromId) continue;
            long k = e.getLongKey();
            e.setValue(survivor);
            byte combined = Rotation3D.compose(migrationRot.get(k), dPrime);
            if (combined != Rotation3D.IDENTITY_BYTE) migrationRot.put(k, combined);
            else migrationRot.remove(k);
        }
        setDirty();
        if (migration.size() == MIGRATION_WARN_THRESHOLD) {
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry migration map reached {} entries; If you barely use Buildstone Toolkit in your world and see this warning, this could indicate a bug.",
                    MIGRATION_WARN_THRESHOLD);
        }
    }

    /** Drop forwarding entries pointing at {@code survivor} (called when it is recycled) so no value ever references a recycled id. */
    private void dropMigrationsTo(long survivor) {
        LongArrayList dead = new LongArrayList();
        for (Long2LongMap.Entry e : migration.long2LongEntrySet()) {
            if (e.getLongValue() == survivor) dead.add(e.getLongKey());
        }
        for (long k : dead) {
            migration.remove(k);
            migrationRot.remove(k);
        }
        if (!dead.isEmpty()) setDirty();
    }

    /** This breaks unloaded proxies from ever getting retargeted by Sable and Create + possibly other issues */
    public void clear() {
        forward.clear();
        reverse.clear();
        refcount.clear();
        rotation.clear();
        migration.clear();
        migrationRot.clear();
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
        rotation.remove(targetId);
        recycleId(targetId);
    }

    /** Cleans registry removing broken entries */
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

        LongArrayList orphanRotation = new LongArrayList();
        for (Long2ByteMap.Entry e : rotation.long2ByteEntrySet()) {
            if (!forward.containsKey(e.getLongKey())) orphanRotation.add(e.getLongKey());
        }
        for (long id : orphanRotation) {
            rotation.remove(id);
        }

        LongArrayList orphanMigration = new LongArrayList();
        for (Long2LongMap.Entry e : migration.long2LongEntrySet()) {
            if (!forward.containsKey(e.getLongValue())) orphanMigration.add(e.getLongKey());
        }
        for (long k : orphanMigration) {
            migration.remove(k);
            migrationRot.remove(k);
        }

        if (!orphanForward.isEmpty() || !orphanRefcount.isEmpty() || !orphanRotation.isEmpty()
                || !orphanMigration.isEmpty()) {
            BuildstoneToolkit.LOGGER.info(
                    "TargetIdRegistry sweep: reclaimed {} orphan forward entries, {} orphan refcount entries, {} orphan rotation entries, {} orphan migration entries.",
                    orphanForward.size(), orphanRefcount.size(), orphanRotation.size(), orphanMigration.size());
        }
    }

    private void recycleId(long id) {
        recycleSet.add(id);
        setDirty();
        if (!recycleSetWarningFired && recycleSet.size() >= RECYCLE_SET_WARN_THRESHOLD) {
            recycleSetWarningFired = true;
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry recycleSet has grown really big: {}. Possible if you have a super complex world, but if you barely use Buildstone Toolkit in your world, please report this as a bug.",
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

        long[] rotationKeys = new long[rotation.size()];
        byte[] rotationValues = new byte[rotation.size()];
        int k = 0;
        for (Long2ByteMap.Entry e : rotation.long2ByteEntrySet()) {
            rotationKeys[k] = e.getLongKey();
            rotationValues[k] = e.getByteValue();
            k++;
        }
        tag.putLongArray(NBTRotationKeysKey, rotationKeys);
        tag.putByteArray(NBTRotationValuesKey, rotationValues);

        long[] migrationKeys = new long[migration.size()];
        long[] migrationValues = new long[migration.size()];
        byte[] migrationRotValues = new byte[migration.size()];
        int m = 0;
        for (Long2LongMap.Entry e : migration.long2LongEntrySet()) {
            migrationKeys[m] = e.getLongKey();
            migrationValues[m] = e.getLongValue();
            migrationRotValues[m] = migrationRot.get(e.getLongKey());
            m++;
        }
        tag.putLongArray(NBTMigrationKeysKey, migrationKeys);
        tag.putLongArray(NBTMigrationValuesKey, migrationValues);
        tag.putByteArray(NBTMigrationRotKey, migrationRotValues);
        return tag;
    }

    public static TargetIdRegistry load(CompoundTag nbt) {
        TargetIdRegistry registry = new TargetIdRegistry(nbt.getLong(NBTNextIdKey));
        registry.recycleSet.addAll(LongArrayList.wrap(nbt.getLongArray(NBTRecycleSetKey)));

        long[] keys = nbt.getLongArray(NBTForwardKeysKey);
        long[] values = nbt.getLongArray(NBTForwardValuesKey);
        if (keys.length != values.length) {
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry.load: forward map key/value arrays disagree in length ({} vs {}); discarding forward map.",
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
                    "TargetIdRegistry.load: refcount map key/value arrays disagree in length ({} vs {}); discarding refcount map.",
                    refcountKeys.length, refcountValues.length);
        } else {
            for (int i = 0; i < refcountKeys.length; i++) {
                registry.refcount.put(refcountKeys[i], refcountValues[i]);
            }
        }

        long[] rotationKeys = nbt.getLongArray(NBTRotationKeysKey);
        byte[] rotationValues = nbt.getByteArray(NBTRotationValuesKey);
        if (rotationKeys.length != rotationValues.length) {
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry.load: rotation map key/value arrays disagree in length ({} vs {}); discarding rotation map.",
                    rotationKeys.length, rotationValues.length);
        } else {
            for (int i = 0; i < rotationKeys.length; i++) {
                registry.rotation.put(rotationKeys[i], rotationValues[i]);
            }
        }

        long[] migrationKeys = nbt.getLongArray(NBTMigrationKeysKey);
        long[] migrationValues = nbt.getLongArray(NBTMigrationValuesKey);
        byte[] migrationRotValues = nbt.getByteArray(NBTMigrationRotKey);
        if (migrationKeys.length != migrationValues.length || migrationKeys.length != migrationRotValues.length) {
            BuildstoneToolkit.LOGGER.warn(
                    "TargetIdRegistry.load: migration map arrays disagree in length ({}, {}, {}); discarding migration map.",
                    migrationKeys.length, migrationValues.length, migrationRotValues.length);
        } else {
            for (int i = 0; i < migrationKeys.length; i++) {
                registry.migration.put(migrationKeys[i], migrationValues[i]);
                if (migrationRotValues[i] != Rotation3D.IDENTITY_BYTE) {
                    registry.migrationRot.put(migrationKeys[i], migrationRotValues[i]);
                }
            }
        }

        registry.sweepOrphans();
        return registry;
    }
}
