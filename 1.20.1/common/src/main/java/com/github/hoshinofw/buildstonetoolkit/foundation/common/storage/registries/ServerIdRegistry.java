package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class ServerIdRegistry<T extends IdObject> extends SavedData implements IdRegistry<T> {
    private static final String NBTKey = "ServerIdRegistry";
    private static final String NBTNextIdKey = "nextId";
    private static final String NBTRecycleSetKey = "recycleSetArray";
    private static final int RECYCLE_SET_WARN_THRESHOLD = 10_000;

    private boolean recycleSetWarningFired = false;

    private final Long2ObjectOpenHashMap<T> map = new Long2ObjectOpenHashMap<>();
    private final LongOpenHashSet recycleSet = new LongOpenHashSet();
    private long nextId = 0;

    public ServerIdRegistry() {}

    private ServerIdRegistry(long nextId) {
        this.nextId = nextId;
        setDirty();
    }

    public static <V extends IdObject> ServerIdRegistry<V> build() {
        return new ServerIdRegistry<>();
    }

    public static <V extends IdObject> ServerIdRegistry<V> build(long nextId) {
        return new ServerIdRegistry<>(nextId);
    }

    public void recycleAndEnsure(long oldId, T entry) {
        ensureEntry(entry);
        if (oldId == entry.getId()) {
            BuildstoneToolkit.LOGGER.warn("Id recycling error: oldId is the same as the entry's id!");
            return;
        }
        remove(oldId);
        recycleId(oldId);
    }

    @Override
    public void ensureEntry(T entry) {
        IdRegistry.super.ensureEntry(entry);
        if (recycleSet.contains(entry.getId())) {
            recycleSet.remove(entry.getId());
            setDirty();
        }
    }

    public long registerNew(T entry) {
        long id = allocateId();
        getMap().put(id, entry);
        return id;
    }

    private void recycleId(long id) {
        recycleSet.add(id);
        setDirty();
        if (!recycleSetWarningFired && recycleSet.size() >= RECYCLE_SET_WARN_THRESHOLD) {
            recycleSetWarningFired = true;
            BuildstoneToolkit.LOGGER.warn(
                    "ServerIdRegistry recycleSet has grown to {} entries. This is unexpectedly high and may indicate id-allocation fragmentation or a leak in the recycle path. This warning fires once per server session.",
                    recycleSet.size());
        }
    }

    private long allocateId() {
        setDirty();

        while (recycleSet.contains(nextId - 1)) {
            recycleSet.remove(nextId - 1);
            nextId--;
        }

        if (!recycleSet.isEmpty())  {
            LongIterator it = recycleSet.iterator();
            long i = it.nextLong();
            it.remove();

            return i;

        }
        return nextId++;
    }

    @Override
    public @NotNull String getName() {
        return NBTKey;
    }

    @Override
    public @NotNull Long2ObjectOpenHashMap<T> getMap() {
        return map;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putLong(NBTNextIdKey, nextId);
        compoundTag.putLongArray(NBTRecycleSetKey, recycleSet.toLongArray());
        return compoundTag;
    }

    public static <V extends IdObject> ServerIdRegistry<V> load(CompoundTag nbt) {
        ServerIdRegistry<V> registry = new ServerIdRegistry<>(nbt.getLong(NBTNextIdKey));
        registry.recycleSet.addAll(LongArrayList.wrap(nbt.getLongArray(NBTRecycleSetKey)));
        return registry;
    }
}
