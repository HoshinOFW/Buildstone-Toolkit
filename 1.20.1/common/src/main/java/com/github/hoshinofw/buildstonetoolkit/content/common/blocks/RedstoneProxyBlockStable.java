package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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

    public static int computePowerLevel(Level level, BlockPos proxyPos,
                                        BlockState targetState, BlockPos targetPos) {
        int targetPower = 0;
        int proxyPower = level.getBestNeighborSignal(proxyPos);
        if (targetState.hasAnalogOutputSignal()) {
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
    public void neighborChanged(BlockState state, Level level, BlockPos proxyPos, Block block, BlockPos pos2, boolean bl) {
        super.neighborChanged(state, level, proxyPos, block, pos2, bl);
        BlockPos targetPos = getLinkedAbsPos(level, proxyPos);
        BlockState targetState = level.getBlockState(targetPos);

        setSignal(level, proxyPos, computePowerLevel(level, proxyPos, targetState, targetPos));

        Block targetBlock = targetState.getBlock();
        if (targetPos.asLong() == proxyPos.asLong()) return;
        level.neighborChanged(targetState, targetPos, targetBlock, proxyPos, false);
        level.updateNeighborsAt(targetPos, targetBlock);
    }

    @Override
    public void targetUpdated(UpdateListenerProxyBlockEntity be, @NotNull Level level) {
        //BuildstoneToolkit.LOGGER.info("RedstoneProxyBlock#targetUpdated called");
        BlockPos targetPos = be.getLinkedAbsPos();
        BlockPos proxyPos = be.getBlockPos();
        setSignal(level, be.getBlockPos(),
                computePowerLevel(level, proxyPos, level.getBlockState(targetPos), targetPos));
        level.updateNeighborsAt(proxyPos, this);
    }
}