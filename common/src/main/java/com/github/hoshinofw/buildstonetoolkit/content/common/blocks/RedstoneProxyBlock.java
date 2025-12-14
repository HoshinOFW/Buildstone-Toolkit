package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.IdProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RedstoneProxyBlock extends IdProxyBlock<RedstoneProxyBlockEntity> {

    private static final IntegerProperty REDSTONE_LEVEL = IntegerProperty.create("power_level", 0, 15);

    public RedstoneProxyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(REDSTONE_LEVEL, 0));
    }

    @Nullable
    public RedstoneProxyBlockEntity getBlockEntity(Level level, BlockPos proxyPos) {
        return getBlockEntity(level, proxyPos, RedstoneProxyBlockEntity.class);
    }

    @Override
    public boolean isPowered(BlockState state) {
        return state.getValue(REDSTONE_LEVEL) != 0;
    }

    public static int getSignal(BlockState state) {
        return state.getValue(REDSTONE_LEVEL);
    }

    private void setSignal(Level level, BlockPos pos, int value) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this)) {
            level.setBlock(pos, state.setValue(REDSTONE_LEVEL, value), 3);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RedstoneProxyBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_LEVEL);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos proxyPos, Block block, BlockPos pos2, boolean bl) {
        super.neighborChanged(state, level, proxyPos, block, pos2, bl);
        setSignal(level, proxyPos, level.getBestNeighborSignal(proxyPos));
        BlockPos targetPos = getLinkedAbsPos(level, proxyPos);
        BlockState targetState = level.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        //BuildstoneToolkit.LOGGER.info("targetPos: {} targetState: {} targetBlock: {}", targetPos, targetState, targetState.getBlock());
        level.neighborChanged(targetState, targetPos, targetBlock, proxyPos, false);
        level.updateNeighborsAt(targetPos, targetBlock);
    }

    public void targetUpdated(RedstoneProxyBlockEntity be) {
        if (be.getLevel() != null) {
            be.getLevel().updateNeighborsAt(be.getBlockPos(), this);
        }
    }

    @Override
    public @Nullable BlockPos getLinkedAbsPos(@NotNull Level level, BlockPos pos) {
        Optional<RedstoneProxyBlockEntity> optionalBe = level.getBlockEntity(pos, BuildstoneBlockEntities.REDSTONE_PROXY.get());
        if (optionalBe.isPresent()) {
            RedstoneProxyBlockEntity be = optionalBe.get();
            return be.getLinkedAbsPos();
        }
        return null;
    }

    @Override
    public @Nullable BlockPos getLinkedRelPos(@NotNull Level level, BlockPos pos) {
        Optional<RedstoneProxyBlockEntity> optionalBe = level.getBlockEntity(pos, BuildstoneBlockEntities.REDSTONE_PROXY.get());
        if (optionalBe.isPresent()) {
            RedstoneProxyBlockEntity be = optionalBe.get();
            return be.getLinkedRelPos();
        }
        return null;
    }

    @Override
    public Util.FailableResult<BlockPos> parsePos(@NotNull Level level, BlockPos proxyPos, BlockPos inputPos) {
        return new Util.FailableResult<>(inputPos, true);
    }

    @Override
    public boolean setLinkedRelPos(@NotNull Level level, BlockPos pos, BlockPos newRelativeTargetPos) {
        RedstoneProxyBlockEntity be = getBlockEntity(level, pos);
        if (be != null) {
            be.setLinkedRelPos(newRelativeTargetPos);
            return true;
        }
        return false;
    }

    @Override
    public boolean setLinkedAbsPos(Level level, BlockPos pos, BlockPos newTargetPos) {
        RedstoneProxyBlockEntity be = getBlockEntity(level, pos);
        if (be != null) {
            be.setLinkedAbsPos(newTargetPos);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        BlockPos targetPos = this.getLinkedAbsPos(level, blockPos);
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.hasAnalogOutputSignal()) {
            return targetState.getAnalogOutputSignal(level, targetPos);
        } else {
            return level.getDirectSignalTo(targetPos);
        }
    }
}
