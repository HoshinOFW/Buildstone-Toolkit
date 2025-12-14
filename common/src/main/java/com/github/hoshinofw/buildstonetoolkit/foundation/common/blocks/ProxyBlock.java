package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
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

    public abstract boolean isPowered(BlockState state);
    public boolean isPowered(@NotNull Level level, BlockPos pos) {
        return isPowered(level.getBlockState(pos));
    }

    @Override
    public void onPlace(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean bl) {
        super.onPlace(oldState, level, pos, newState, bl);
        neighborChanged(newState, level, pos, newState.getBlock(), pos, bl);
    }

    //Get linked block
    public abstract @Nullable BlockPos getLinkedAbsPos(@NotNull Level level, BlockPos pos);
    public abstract @Nullable BlockPos getLinkedRelPos(@NotNull Level level, BlockPos pos);

    /**
     * Returns if the setting failed or succeeded, but the block's state should always be valid.
     */
    public abstract boolean setLinkedRelPos(@NotNull Level level, BlockPos pos, BlockPos newRelativeTargetPos);
    /**
     * Returns if setting the position failed or not, but the block's state should always be valid.
     */
    public boolean setLinkedAbsPos(Level level, BlockPos pos, BlockPos newTargetBlockPos) {
        Util.FailableResult<BlockPos> parsedResult = parsePos(level, pos, newTargetBlockPos);
        BlockPos parsedPos = parsedResult.value();

        return setLinkedRelPos(level, pos, parsedPos) && parsedResult.succeeded();
    }

    @Nullable
    public BlockState getLinkedBlockState(@NotNull Level level, BlockPos pos) {
        return level.getBlockState(this.getLinkedAbsPos(level, pos));
    }

    /**
     * Parses position based on the proxy's logic. This is where limitations to what a proxy can be linked to are defined.
     */
    public abstract Util.FailableResult<BlockPos> parsePos(@NotNull Level level, BlockPos proxyPos, BlockPos inputPos);
}
