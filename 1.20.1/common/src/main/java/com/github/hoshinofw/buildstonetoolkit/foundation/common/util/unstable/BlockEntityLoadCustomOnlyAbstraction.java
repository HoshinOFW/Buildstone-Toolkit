package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.unstable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityLoadCustomOnlyAbstraction {
    public static void loadCustomOnly(BlockEntity be, CompoundTag tag, Level level) {
        be.load(tag);
    }

}
