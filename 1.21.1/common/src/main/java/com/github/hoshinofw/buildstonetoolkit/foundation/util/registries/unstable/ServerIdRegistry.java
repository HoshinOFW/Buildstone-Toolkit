package com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdRegistry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class ServerIdRegistry<T extends IdObject> extends SavedData implements IdRegistry<T> {
    private static final String name = "ServerIdRegistry";
    private final Long2ObjectOpenHashMap<T> map = new Long2ObjectOpenHashMap<>();
    private long nextId = 0;

    public ServerIdRegistry() {}

    private ServerIdRegistry(long nextId) {
        this.nextId = nextId;
        setDirty();
    }

    public long registerNew(T entry) {
        long id = allocateID();
        getMap().put(id, entry);
        return id;
    }

    public long allocateID() {
        long output = this.getNextId();
        this.incrementNextId();
        return output;
    }

    private long getNextId() {
        return nextId;
    }

    private void incrementNextId() {
        this.nextId++;
        setDirty();
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Long2ObjectOpenHashMap<T> getMap() {
        return map;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putLong("nextId", nextId);
        return compoundTag;
    }

    public static <V extends IdObject> ServerIdRegistry<V> load(CompoundTag nbt, HolderLookup.Provider registries) {
        return new ServerIdRegistry<>(nbt.getLong("nextId"));
    }
}
