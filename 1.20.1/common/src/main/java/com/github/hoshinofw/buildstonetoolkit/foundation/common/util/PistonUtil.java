package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.jetbrains.annotations.NotNull;

public class PistonUtil {

    public static void doProxyUpdatingLogic(@NotNull Level level, PistonMovingBlockEntity mbe, Direction moveDirection, BlockPos originalPos, BlockPos finalPos) {
        if (mbe.getMovedState().getBlock() instanceof PistonProxyBlock pistonProxyBlock) {
            level.setBlock(finalPos, mbe.getMovedState(), Block.UPDATE_ALL);

            BlockEntity blockEntity = level.getBlockEntity(finalPos);
            CompoundTag savedTag = Util.getProxyTag(mbe);

            if (blockEntity != null && savedTag != null) {
                if (!pistonProxyBlock.shouldPreserveTargetAbsPos(level, mbe, originalPos, finalPos, moveDirection)) {
                    pistonProxyBlock.saveShiftedTargetAbsPosToNBT(level, finalPos, mbe.getMovedState(), savedTag, moveDirection);
                }
                NBTUtil.loadCustomOnly(blockEntity, savedTag, level);
                blockEntity.setChanged();
            }
        }
    }

}
