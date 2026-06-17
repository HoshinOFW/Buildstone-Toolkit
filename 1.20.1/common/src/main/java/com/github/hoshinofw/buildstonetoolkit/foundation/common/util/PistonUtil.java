package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PistonUtil {

    public static void doProxyUpdatingLogic(@NotNull Level level, PistonMovingBlockEntity mbe, Direction moveDirection, BlockPos originalPos, BlockPos finalPos) {
        if (mbe.getMovedState().getBlock() instanceof PistonProxyBlock pistonProxyBlock) {
            CompoundTag savedTag = Util.getProxyTag(mbe);
            
            if (savedTag == null) return;

            level.setBlock(finalPos, mbe.getMovedState(), Block.UPDATE_ALL);

            BlockEntity blockEntity = level.getBlockEntity(finalPos);

            if (blockEntity != null) {
                if (!pistonProxyBlock.shouldPreserveTargetAbsPos(level, mbe, originalPos, finalPos, moveDirection)) {
                    pistonProxyBlock.saveShiftedTargetAbsPosToNBT(level, finalPos, mbe.getMovedState(), savedTag, moveDirection);
                }
                NBTUtil.loadCustomOnly(blockEntity, savedTag, level);
                blockEntity.setChanged();
            }
        }
    }

    public static boolean isPushableBlockEntity(BlockState state) {
        return (state.hasBlockEntity() && (state.getBlock() instanceof PistonProxyBlock));
    }
}
