package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public abstract class ServerIdRegistry<T extends IdObject> extends SavedData implements IdRegistry<T> {

    @ShadowVersion
    private long nextId;

    @ShadowVersion
    public static native <V extends IdObject> ServerIdRegistry<V> build();

    @ShadowVersion
    public static native <V extends IdObject> ServerIdRegistry<V> build(long nextId);

    @ShadowVersion
    @ModifySignature("save")
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider);

    @ShadowVersion
    @ModifySignature("load")
    public static <V extends IdObject> ServerIdRegistry<V> load(CompoundTag nbt, HolderLookup.Provider registries);
}
