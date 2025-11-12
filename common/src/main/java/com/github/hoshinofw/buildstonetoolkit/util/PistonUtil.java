package com.github.hoshinofw.buildstonetoolkit.util;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.IProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.IProxyEntityBlock;
import com.github.hoshinofw.buildstonetoolkit.util.mixin.PistonMovingBlockEntityMixinInterface;
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
        if (mbe.getMovedState().getBlock() instanceof IProxyBlock proxyBlock) {
            if (proxyBlock.shouldPreserveTargetAbsPos(level, mbe, originalPos, finalPos, moveDirection)) {
                BlockState newState;
                if (proxyBlock instanceof IProxyEntityBlock entityBlock) {
                    newState = entityBlock.computeNewStateWithPreservedTargetAbsPos(level, originalPos,
                            Util.getProxyTag(mbe), mbe.getMovedState(), moveDirection);
                    level.setBlock(finalPos, newState, Block.UPDATE_ALL);
                    proxyBlock.neighborChanged(newState, level, finalPos, ((Block)proxyBlock), finalPos, true);

                    BlockEntity blockEntity = level.getBlockEntity(finalPos);
                    CompoundTag savedTag = Util.getProxyTag(mbe);
                    if (blockEntity != null && savedTag != null) {
                        CompoundTag newNBT = entityBlock.computeNewNBTWithPreservedTargetAbsPos(level, finalPos, newState, savedTag, moveDirection);
                        blockEntity.loadCustomOnly(newNBT, level.registryAccess());
                        blockEntity.setChanged();
                    }
                } else {
                    newState = proxyBlock.computeNewStateWithPreservedTargetAbsPos(level, originalPos, mbe.getMovedState(), moveDirection);
                    level.setBlock(finalPos, newState, Block.UPDATE_ALL);
                    proxyBlock.neighborChanged(newState, level, finalPos, ((Block)proxyBlock), finalPos, true);
                }
            } else {
                level.setBlock(finalPos, mbe.getMovedState(), Block.UPDATE_ALL);
                proxyBlock.neighborChanged(mbe.getMovedState(), level, finalPos, ((Block)proxyBlock), finalPos, true);
                if (proxyBlock instanceof IProxyEntityBlock) {
                    BlockEntity blockEntity = level.getBlockEntity(finalPos);
                    CompoundTag savedTag = ((PistonMovingBlockEntityMixinInterface) mbe).buildstonetoolkit$getProxyTag();
                    if (blockEntity != null && savedTag != null) {
                        blockEntity.loadCustomOnly(savedTag, level.registryAccess());
                        blockEntity.setChanged();
                    }
                }
            }
        }
    }

}
