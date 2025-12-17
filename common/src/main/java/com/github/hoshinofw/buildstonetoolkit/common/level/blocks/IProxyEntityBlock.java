package com.github.hoshinofw.buildstonetoolkit.common.level.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IProxyEntityBlock {

    public CompoundTag computeNewNBTWithPreservedTargetAbsPos(Level level, BlockPos pos, BlockState state, @Nullable CompoundTag nbt, Direction moveDirection);
    BlockState computeNewStateWithPreservedTargetAbsPos(Level level, BlockPos pos, CompoundTag nbt, BlockState state, Direction moveDirection);

}
