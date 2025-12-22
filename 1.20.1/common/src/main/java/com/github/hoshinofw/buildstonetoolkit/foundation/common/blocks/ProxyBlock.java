package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ProxyBlock extends Block {
    public ProxyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean bl) {
        super.onPlace(oldState, level, pos, newState, bl);
        neighborChanged(newState, level, pos, newState.getBlock(), pos, bl);
    }

    //Get linked block

    public abstract @NotNull BlockPos getLinkedAbsPos(@NotNull Level level, BlockPos pos);
    public abstract @NotNull BlockPos getLinkedRelPos(@NotNull Level level, BlockPos pos);

    @Nullable
    public BlockState getLinkedBlockState(@NotNull Level level, BlockPos pos) {
        return level.getBlockState(this.getLinkedAbsPos(level, pos));
    }
}
