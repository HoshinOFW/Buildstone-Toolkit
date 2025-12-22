package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
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

public class RedstoneProxyBlock extends UpdateListenerProxyBlock {

    private static final IntegerProperty REDSTONE_LEVEL = IntegerProperty.create("power_level", 0, 15);

    public static RedstoneProxyBlock getBlock() {
        return BuildstoneBlocks.REDSTONE_PROXY.get();
    }

    public RedstoneProxyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(REDSTONE_LEVEL, 0));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.redstone_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
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
        //BuildstoneToolkit.LOGGER.info("RedstoneProxyBlock#neighborChanged called, targetPos: {} targetState: {} targetBlock: {}", targetPos, targetState, targetState.getBlock());
        if (targetPos.asLong() == proxyPos.asLong()) return;
        level.neighborChanged(targetState, targetPos, targetBlock, proxyPos, false);
        level.updateNeighborsAt(targetPos, targetBlock);
    }

    @Override
    public void targetUpdated(UpdateListenerProxyBlockEntity be, @NotNull Level level) {
        //BuildstoneToolkit.LOGGER.info("RedstoneProxyBlock#targetUpdated called");
        level.updateNeighborsAt(be.getBlockPos(), this);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        BlockPos targetPos = this.getLinkedAbsPos(level, blockPos);
        if (targetPos.asLong() == blockPos.asLong()) return 0;
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.hasAnalogOutputSignal()) {
            return targetState.getAnalogOutputSignal(level, targetPos);
        } else {
            return level.getDirectSignalTo(targetPos);
        }
    }
}
