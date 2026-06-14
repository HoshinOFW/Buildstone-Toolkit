package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

@ModifyClass
public abstract class TargetIdRegistry extends SavedData {

    @ShadowVersion
    public static final long NO_TARGET_ID;


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
    public long retarget(long oldPos, long newPos);

    @ShadowVersion
    public long reverseLookup(long pos);

    @ShadowVersion
    public Rotation3D getRotation(long targetId);

    @ShadowVersion
    public void addRotation(long targetId, Rotation3D event);

    @ShadowVersion
    public static boolean isValidTargetId(long targetId);

    }
