package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.multiversion.OverwriteVersion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NBTUTIL {

    @OverwriteVersion
    public static CompoundTag saveWithoutMetadata(BlockEntity be, Level level) {
        return be.saveWithoutMetadata(level.registryAccess());
    }

    @OverwriteVersion
    public static void loadCustomOnly(BlockEntity be, CompoundTag tag, Level level) {
        be.loadCustomOnly(tag, level.registryAccess());
    }
}
