package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

@ModifyClass
public abstract class TargetIdRegistry extends SavedData {

    @ShadowVersion
    public static TargetIdRegistry build();;


    @ShadowVersion
    @ModifySignature("save")
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider);

    @ShadowVersion
    @ModifySignature("load")
    public static TargetIdRegistry load(CompoundTag nbt, HolderLookup.Provider registries);

    @ShadowVersion
    public void retarget(long oldPos, long newPos);


    }
