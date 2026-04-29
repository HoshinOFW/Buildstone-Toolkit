package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdObject;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NBTUtil {

    @OverwriteVersion
    public static CompoundTag saveWithoutMetadata(BlockEntity be, Level level) {
        return be.saveWithoutMetadata(level.registryAccess());
    }

    @OverwriteVersion
    public static void loadCustomOnly(BlockEntity be, CompoundTag tag, Level level) {
        be.loadCustomOnly(tag, level.registryAccess());
    }

    @ShadowVersion
    public static void saveId(CompoundTag nbt, IdObject object);

    @ShadowVersion
    public static Long getTargetPosFromNBT(CompoundTag nbt);

    @ShadowVersion
    public static void saveTargetNBTFromProxy(CompoundTag nbt, ProxyBlockEntity<?> proxy);

}
