package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdObject;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NBTUtil {

    @ShadowVersion
    public static final String NBTIdKey;

    @OverwriteVersion
    public static CompoundTag saveWithoutId(BlockEntity be, Level level) {
        CompoundTag nbt = be.saveWithoutMetadata(level.registryAccess());
        nbt.remove(NBTIdKey);
        return be.saveWithoutMetadata(level.registryAccess());
    }

    @OverwriteVersion
    public static CompoundTag saveWithId(BlockEntity be, Level level) {
        return be.saveWithoutMetadata(level.registryAccess());

    }

    @OverwriteVersion
    public static void loadCustomOnly(BlockEntity be, CompoundTag tag, Level level) {
        be.loadCustomOnly(tag, level.registryAccess());
    }

    @ShadowVersion
    public static void saveId(CompoundTag nbt, IdObject object);

    @ShadowVersion
    public static Long getAbsoluteTargetPosFromNBT(CompoundTag nbt);

    @ShadowVersion
    public static void saveAbsoluteTargetNBTFromProxy(CompoundTag nbt, ProxyBlockEntity<?, ?> proxy);
}
