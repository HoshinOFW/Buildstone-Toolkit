package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.unstable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntitySaveWithoutMetadataAbstraction {

    public static CompoundTag saveWithoutMetadata(BlockEntity be, Level level) {
        return be.saveWithoutMetadata(level.registryAccess());
    }

}
