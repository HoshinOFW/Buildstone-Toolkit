package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RedstoneProxyBlockStable extends UpdateListenerProxyBlock {

    private static final IntegerProperty REDSTONE_LEVEL = IntegerProperty.create("power_level", 0, 15);

    public static RedstoneProxyBlock getBlock() {
        return BuildstoneBlocks.REDSTONE_PROXY.get();
    }

    public RedstoneProxyBlockStable(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(REDSTONE_LEVEL, 0));
    }

    public static int getSignal(BlockState state) {
        return state.getValue(REDSTONE_LEVEL);
    }

    private void setSignal(Level level, BlockPos proxyPos, int newSignal) {
        BlockState oldState = level.getBlockState(proxyPos);
        setSignal(level, proxyPos, oldState, newSignal);
    }

    private void setSignal(Level level, BlockPos proxyPos, BlockState oldState, int newSignal) {
        if (oldState.is(this) && getSignal(oldState) != newSignal) {
            level.setBlock(proxyPos, oldState.setValue(REDSTONE_LEVEL, newSignal), Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RedstoneProxyBlockEntity(blockPos, blockState);
    }

    public int computePowerLevel(Level level, BlockPos proxyPos, BlockState targetState, BlockPos targetPos) {
        int targetPower = 0;
        int proxyPower = level.getBestNeighborSignal(proxyPos);
        if (proxyPos.asLong() == targetPos.asLong()) {
            targetPower = level.getDirectSignalTo(targetPos);
        } else if (targetState.hasAnalogOutputSignal()) {
            targetPower = targetState.getAnalogOutputSignal(level, targetPos);
        } else if (targetState.isRedstoneConductor(level, targetPos)) {
            targetPower = level.getDirectSignalTo(targetPos);
        }
        return Math.max(targetPower, proxyPower);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_LEVEL);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos proxyPos, Block neighboringBlock, BlockPos pos2, boolean bl) {
        super.neighborChanged(state, level, proxyPos, neighboringBlock, pos2, bl);
        BlockPos targetPos = getLinkedAbsPos(level, proxyPos);
        BlockState targetState = level.getBlockState(targetPos);

        setSignal(level, proxyPos, state, computePowerLevel(level, proxyPos, targetState, targetPos));

        if (targetPos.asLong() == proxyPos.asLong()) return;
        level.neighborChanged(targetState, targetPos, this, targetPos, false);
        level.updateNeighborsAt(targetPos, this);
    }

    @Override
    public void targetUpdated(UpdateListenerProxyBlockEntity be, @NotNull Level level) {
        BlockPos targetPos = be.getLinkedAbsPos();
        BlockPos proxyPos = be.getBlockPos();
        setSignal(level, be.getBlockPos(), computePowerLevel(level, proxyPos, level.getBlockState(targetPos), targetPos));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockPos oldTargetPos = null;

        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            UpdateListenerProxyBlockEntity be = getBlockEntity(level, pos);
            oldTargetPos = be.getLinkedAbsPos();
        }

        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide() || oldTargetPos == null) return;

        BlockState targetState = level.getBlockState(oldTargetPos);

        level.updateNeighborsAt(oldTargetPos, state.getBlock());
        level.neighborChanged(targetState, oldTargetPos, state.getBlock(), pos, false);
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide()) return;

        level.scheduleTick(pos, this, 1, TickPriority.HIGH);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos targetPos = getLinkedAbsPos(level, pos);
        BlockState targetState = level.getBlockState(targetPos);

        setSignal(level, pos, state, computePowerLevel(level, pos, targetState, targetPos));

        if (!targetPos.equals(pos)) {
            level.updateNeighborsAt(targetPos, this);
            level.neighborChanged(targetState, targetPos, this, pos, false);
        }
    }


    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return getSignal(blockState);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }
}