package com.github.hoshinofw.buildstonetoolkit.common.level.blocks;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.entity.GuidedProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class GuidedProxy extends Block implements IProxyBlock, IProxyEntityBlock, EntityBlock {

    public static final DirectionProperty LINK_DIRECTION = DirectionProperty.create("link_direction");

    public GuidedProxy(BlockBehaviour.Properties properties){
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(LINK_DIRECTION, Direction.UP)
                .setValue(POWER_LEVEL, 0));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.guided_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LINK_DIRECTION).add(POWER_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(LINK_DIRECTION, blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    public static Direction getDirection(@NotNull Level level, BlockPos proxyPos) {
        return level.getBlockState(proxyPos).getValue(LINK_DIRECTION);
    }

    public static void setDirection(@NotNull Level level, BlockPos proxyPos, Direction direction) {
        level.setBlock(proxyPos, level.getBlockState(proxyPos).setValue(LINK_DIRECTION, direction), 3);
    }

    public static int getDistance(@NotNull Level level, BlockPos proxyPos) {
        return ((GuidedProxyBlockEntity) Objects.requireNonNull(level.getBlockEntity(proxyPos))).getDistance();
    }

    public static void setDistance(@NotNull Level level, BlockPos proxyPos, int newDistance) {
        if (newDistance == 0 ) {
            ((GuidedProxyBlockEntity) Objects.requireNonNull(level.getBlockEntity(proxyPos))).setDistance(newDistance);
            return;
        }
        if (newDistance > 0) {
            ((GuidedProxyBlockEntity) Objects.requireNonNull(level.getBlockEntity(proxyPos))).setDistance(newDistance);
            setDirection(level, proxyPos, Util.getPositiveDirection(level.getBlockState(proxyPos).getValue(LINK_DIRECTION).getAxis()));
            return;
        }
        ((GuidedProxyBlockEntity) Objects.requireNonNull(level.getBlockEntity(proxyPos))).setDistance(-1 * newDistance);
        setDirection(level, proxyPos, Util.getPositiveDirection(level.getBlockState(proxyPos).getValue(LINK_DIRECTION).getAxis()).getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bl) {
        IProxyBlock.super.neighborChanged(state, level, pos, block, pos2, bl);
    }

    @Override
    public void onPlace(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean bl) {
        IProxyBlock.super.onPlace(oldState, level, pos, newState, bl);
    }

    @Override
    public boolean shouldPreserveTargetAbsPos(@NotNull Level level, @NotNull PistonMovingBlockEntity mbe, BlockPos originalPos, BlockPos finalPos, Direction moveDirection) {
        if (isStronglyPowered(mbe.getMovedState())) {return true;}
        return isWeaklyPowered(level, finalPos);
    }

    @Override
    public boolean shouldTransferMovement(BlockPos pos, BlockState state, Direction blockLineRecursionDirection) {
        return getPowerLevel(state) == 0;
    }

    @Override
    public BlockPos getLinkedBlockPos(@NotNull Level level, BlockPos blockPos) {
        return blockPos.relative(level.getBlockState(blockPos).getValue(LINK_DIRECTION), ((GuidedProxyBlockEntity) Objects.requireNonNull(level.getBlockEntity(blockPos))).getDistance());
    }

    @Override
    public Util.FailableResult<BlockPos> parsePos(@NotNull Level level, BlockPos proxyPos, BlockPos inputPos) {
        BlockPos rawPos = inputPos.subtract(proxyPos);
        Direction.Axis proxyAxis = getDirection(level, proxyPos).getAxis();
        BlockPos processedPos = Util.projectOntoAxis(rawPos, proxyAxis);

        if (Util.deepEquals(rawPos, processedPos)) {return new Util.FailableResult<>(processedPos, true);}

        else {return new Util.FailableResult<>(processedPos, false);}
    }

    @Override
    public boolean setLinkedRelPos(@NotNull Level level, BlockPos proxyPos, BlockPos newRelativeTargetPos) {
        Direction.Axis proxyAxis = getDirection(level, proxyPos).getAxis();
        int posValueInAxis = newRelativeTargetPos.get(proxyAxis);
        setDistance(level, proxyPos, posValueInAxis);

        return Util.isNonZeroOnlyOn(newRelativeTargetPos, proxyAxis);
    }

    @Override
    public BlockState computeNewStateWithPreservedTargetAbsPos(Level level, BlockPos pos, BlockState state, Direction moveDirection) {
        return null;
    }

    @Override
    public BlockState computeNewStateWithPreservedTargetAbsPos(Level level, BlockPos pos, CompoundTag nbt, BlockState state, Direction moveDirection) {
        int newDistance = computeOffset(state, nbt, moveDirection, 1);
        //BuildstoneToolkit.LOGGER.info("computeOffset returned newDistance: {}", newDistance);
        BlockState newState = defaultBlockState();
        if (newDistance >= 0) {
            newState = newState.setValue(LINK_DIRECTION, state.getValue(LINK_DIRECTION));
        } else {
            newState = newState.setValue(LINK_DIRECTION, moveDirection.getOpposite());
        }
        return newState;
    }

    @Override
    public CompoundTag computeNewNBTWithPreservedTargetAbsPos(Level level, BlockPos pos, BlockState state, @Nullable CompoundTag nbt, Direction moveDirection) {
        assert nbt != null;
        CompoundTag nbtOutput = nbt.copy();

        int newDistance = computeOffset(state, nbt, moveDirection, 1);

        nbtOutput.putInt("link_distance", newDistance);

        return nbtOutput;
    }

    @Override
    public void offsetLinkedPos(Level level, BlockPos pos, BlockState state, Direction direction) {
        GuidedProxyBlockEntity blockEntity = (GuidedProxyBlockEntity)level.getBlockEntity(pos);
        if (blockEntity == null) {return;}

        BlockState newState = state;
        int newDistance = computeOffset(state, blockEntity, direction, 1);
        if (newDistance >= 0) {
            blockEntity.setDistance(newDistance);
        } else {
            blockEntity.setDistance(1);
            newState = state.setValue(LINK_DIRECTION, direction.getOpposite());
        }
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.setBlockEntity(blockEntity);
    }

    @Override
    public void offsetLinkedPos(Level level, BlockPos pos, BlockState state, Direction direction, int i) {
        GuidedProxyBlockEntity blockEntity = (GuidedProxyBlockEntity)level.getBlockEntity(pos);
        if (blockEntity == null) {return;}

        BlockState newState = state;
        int newDistance = computeOffset(state, blockEntity, direction, i);
        if (newDistance >= 0) {
            blockEntity.setDistance(newDistance);
        } else {
            blockEntity.setDistance(1);
            newState = state.setValue(LINK_DIRECTION, direction.getOpposite());
        }
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.setBlockEntity(blockEntity);
    }

    public static int computeOffset(BlockState state, GuidedProxyBlockEntity ppbe, Direction offsetDirection, int i) {
        return i * (ppbe.getDistance() + compareDirections(offsetDirection, state.getValue(LINK_DIRECTION)));
    }

    public static int computeOffset(BlockState state, CompoundTag ppbeTag, Direction offsetDirection, int i) {
        return i * (ppbeTag.getInt("link_distance") + compareDirections(offsetDirection, state.getValue(LINK_DIRECTION)));
    }

    public static int compareDirections(Direction a, Direction b) {
        if (a == b.getOpposite()) {
            return 1;  // opposite
        } else if (a == b) {
            return -1; // same
        } else {
            return 0;  // neither
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GuidedProxyBlockEntity(blockPos, blockState);
    }
}
