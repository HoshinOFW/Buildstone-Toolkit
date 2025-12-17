package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.ObserverProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObserverProxyBlock extends UpdateListenerProxyBlock {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final DirectionProperty FACING = DirectionProperty.create("facing");

    public ObserverProxyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext arg) {
        return this.defaultBlockState().setValue(FACING, arg.getNearestLookingDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
    }

    @Override
    public void targetUpdated(UpdateListenerProxyBlockEntity be, @NotNull Level level) {
        if (!be.getBlockState().getValue(POWERED)) {
            this.startSignal(level, be.getBlockPos());
        }
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter blockGetter, BlockPos proxyPos, Direction queryDirection) {
        return state.getValue(POWERED) && state.getValue(FACING) == queryDirection ? 15 : 0;
    }

    @Override
    protected boolean isSignalSource(BlockState blockState) {
        return true;
    }

    private void startSignal(LevelAccessor levelAccessor, BlockPos proxyPos) {
        if (!levelAccessor.isClientSide() && !levelAccessor.getBlockTicks().hasScheduledTick(proxyPos, this)) {
            levelAccessor.scheduleTick(proxyPos, this, 2);
        }
    }

    @Override
    protected void tick(BlockState arg, ServerLevel arg2, BlockPos arg3, RandomSource arg4) {
        if (arg.getValue(POWERED)) {
            arg2.setBlock(arg3, arg.setValue(POWERED, false), 2);
        } else {
            arg2.setBlock(arg3, arg.setValue(POWERED, true), 2);
            arg2.scheduleTick(arg3, this, 2);
        }

        this.updateNeighborsInFront(arg2, arg3, arg);
    }

    protected void updateNeighborsInFront(Level arg, BlockPos arg2, BlockState arg3) {
        Direction direction = arg3.getValue(FACING);
        BlockPos blockPos = arg2.relative(direction.getOpposite());
        arg.neighborChanged(blockPos, this, arg2);
        arg.updateNeighborsAtExceptFromFacing(blockPos, this, direction);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ObserverProxyBlockEntity(blockPos, blockState);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.observer_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }
}
