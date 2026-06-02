package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.compat.DirectionalAnalogOutputSourcesRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstoneProxyBlock extends UpdateListenerProxyBlock<RedstoneProxyBlock, RedstoneProxyBlockEntity> implements FaceTargetingProxyBlock {
    private static final IntegerProperty REDSTONE_LEVEL = IntegerProperty.create("power_level", 0, 15);
    private static final EnumProperty<PROXY_MODE> MODE = EnumProperty.create("mode", PROXY_MODE.class);
    private static final EnumProperty<TargetFace> TARGET_FACE = EnumProperty.create("target_face", TargetFace.class);

    public static RedstoneProxyBlock getBlock() {
        return BuildstoneBlocks.REDSTONE_PROXY.get();
    }

    public RedstoneProxyBlock(Properties properties) {
        super(properties, RedstoneProxyBlockEntity.class);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(REDSTONE_LEVEL, 0)
                .setValue(MODE, PROXY_MODE.READ)
                .setValue(TARGET_FACE, TargetFace.ALL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_LEVEL).add(MODE).add(TARGET_FACE);
    }

    public int getSignal(BlockState state) {
        return state.getValue(REDSTONE_LEVEL);
    }

    private boolean setSignal(Level level, BlockPos proxyPos, int newSignal) {
        BlockState oldState = level.getBlockState(proxyPos);
        return setSignal(level, proxyPos, oldState, newSignal);
    }

    public static @NotNull PROXY_MODE getMode(BlockState state) {
        return state.getValue(MODE);
    }

    public static void cycleMode(Level level, BlockPos proxyPos, BlockState oldState) {
        BlockState newState = oldState.cycle(MODE);
        level.setBlock(proxyPos, newState, Block.UPDATE_ALL);
        if (!level.isClientSide()) {
            getBlock().neighborChanged(newState, level, proxyPos, getBlock(), proxyPos, false);
        }
        //neighborChanged already updates target when the proxy is on write mode. However a switch to read requires an update
        if (newState.hasProperty(MODE) && newState.getValue(MODE) == PROXY_MODE.READ) {
            RedstoneProxyBlock block = getBlock();
            BlockPos targetPos = block.getLinkedAbsPos(level, proxyPos);
            level.neighborChanged(level.getBlockState(targetPos), targetPos, block, targetPos, false);
            level.updateNeighborsAt(targetPos, block);
        }
    }

    @Override
    public @NotNull TargetFace getTargetFace(BlockState state) {
        return state.getValue(TARGET_FACE);
    }

    @Override
    public void setTargetFace(Level level, BlockPos pos, BlockState state, TargetFace targetFace) {
        BlockState newState = state.setValue(TARGET_FACE, targetFace);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        getBlock().neighborChanged(newState, level, pos, getBlock(), pos, false);
    }

    @Override
    public BlockState setTargetFace(BlockState state, TargetFace targetFace) {
        return state.setValue(TARGET_FACE, targetFace);
    }

    /**
     * Returns true if the signal was actually changed, false if not.
     */
    private boolean setSignal(Level level, BlockPos proxyPos, BlockState oldState, int newSignal) {
        if (oldState.is(this) && getSignal(oldState) != newSignal) {
            level.setBlock(proxyPos, oldState.setValue(REDSTONE_LEVEL, newSignal), Block.UPDATE_ALL);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RedstoneProxyBlockEntity(blockPos, blockState);
    }

    public int transmittedSignal(BlockState proxyState, @Nullable Direction direction, boolean isQueryingPos) {
        if (getMode(proxyState) != PROXY_MODE.WRITE) return 0;
        TargetFace face = proxyState.getValue(TARGET_FACE);

        if (face == TargetFace.ALL) return getSignal(proxyState);

        if (isQueryingPos &&
                ( direction == null || TargetFace.fromDirection(direction) == face)) return getSignal(proxyState);

        return 0;
    }

    public int computePowerLevel(Level level, BlockState proxyState ,BlockPos proxyPos, BlockState targetState, BlockPos targetPos) {
        if (proxyState.hasProperty(MODE) && proxyState.getValue(MODE) == PROXY_MODE.WRITE) return level.getBestNeighborSignal(proxyPos);

        if (proxyPos.asLong() == targetPos.asLong()) return level.getBestNeighborSignal(proxyPos);

        TargetFace face = proxyState.getValue(TARGET_FACE);

        if (face == TargetFace.ALL) {
            if (targetState.hasAnalogOutputSignal()) {
                return targetState.getAnalogOutputSignal(level, targetPos);
            }
            return level.getBestNeighborSignal(targetPos);
        } else {
            //This section mimics how a real comparator works + checking a directional comparator output registry (mainly for Simulated compat)
            Direction targetingDirection = face.toDirection();
            assert targetingDirection != null;
            Direction oppositeTargeting = targetingDirection.getOpposite();

            if (targetState.hasAnalogOutputSignal()) {
                if (!DirectionalAnalogOutputSourcesRegistry.isEmpty()) {
                    return Math.max(targetState.getAnalogOutputSignal(level, targetPos),
                            DirectionalAnalogOutputSourcesRegistry.get(targetState, level, targetPos, oppositeTargeting));
                }
                return targetState.getAnalogOutputSignal(level, targetPos);
            } else {

                BlockPos adjacentPos = targetPos.relative(oppositeTargeting);
                BlockState adjacentState = level.getBlockState(adjacentPos);
                if (targetState.isRedstoneConductor(level, targetPos) && adjacentState.hasAnalogOutputSignal()) {

                    if (!DirectionalAnalogOutputSourcesRegistry.isEmpty()) {
                        return Math.max(adjacentState.getAnalogOutputSignal(level, adjacentPos),
                                DirectionalAnalogOutputSourcesRegistry.get(adjacentState, level, adjacentPos, oppositeTargeting));
                    }
                    return adjacentState.getAnalogOutputSignal(level, adjacentPos);
                }

            }
            return level.getSignal(targetPos, oppositeTargeting);
        }
    }

    @Override
    public void neighborChanged(BlockState proxyState, Level level, BlockPos proxyPos, Block neighboringBlock, BlockPos pos2, boolean bl) {
        super.neighborChanged(proxyState, level, proxyPos, neighboringBlock, pos2, bl);

        if (!hasProxyBlockEntity(level, proxyPos)) return;
        BlockPos targetPos = getLinkedAbsPos(level, proxyPos);
        BlockState targetState = level.getBlockState(targetPos);
        if (setSignal(level, proxyPos, proxyState, computePowerLevel(level, proxyState, proxyPos, targetState, targetPos))
                && targetPos.asLong() != proxyPos.asLong()) {
            if (proxyState.hasProperty(MODE) && proxyState.getValue(MODE) == PROXY_MODE.WRITE) {
                level.neighborChanged(targetState, targetPos, this, targetPos, false);
                level.updateNeighborsAt(targetPos, this);
            }
        }
    }

    @Override
    public void targetUpdated(UpdateListenerProxyBlockEntity<?, ?> be, Level level) {
        BlockPos targetPos = be.getLinkedAbsPos();
        BlockState proxyState = be.getBlockState();
        //BuildstoneToolkit.LOGGER.info("RgetUpdated");

        if (proxyState.hasProperty(MODE) && proxyState.getValue(MODE) != PROXY_MODE.READ) return;

        setSignal(level, be.getBlockPos(), computePowerLevel(level, be.getBlockState(), be.getBlockPos(), level.getBlockState(targetPos), targetPos));

    }

    @Override
    public void onRemove(BlockState proxyState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockPos oldTargetPos = null;

        if (!level.isClientSide() && proxyState.getBlock() != newState.getBlock() && hasProxyBlockEntity(level, pos)) {
            RedstoneProxyBlockEntity be = getProxyBlockEntity(level, pos);
            oldTargetPos = be.getLinkedAbsPos();
        }

        super.onRemove(proxyState, level, pos, newState, isMoving);

        if (level.isClientSide() || oldTargetPos == null) return;

        BlockState targetState = level.getBlockState(oldTargetPos);
        if (proxyState.getValue(MODE) != PROXY_MODE.WRITE) return;

        level.updateNeighborsAt(oldTargetPos, proxyState.getBlock());
        level.neighborChanged(targetState, oldTargetPos, proxyState.getBlock(), pos, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide()) return;

        level.scheduleTick(pos, this, 1, TickPriority.HIGH);
    }

    @Override
    public void tick(BlockState proxyState, ServerLevel level, BlockPos proxyPos, RandomSource random) {
        BlockPos targetPos = getLinkedAbsPos(level, proxyPos);
        BlockState targetState = level.getBlockState(targetPos);

        setSignal(level, proxyPos, proxyState, computePowerLevel(level, proxyState, proxyPos, targetState, targetPos));

        if (!targetPos.equals(proxyPos)) {
            if (proxyState.getValue(MODE) != PROXY_MODE.WRITE) return;
            level.updateNeighborsAt(targetPos, this);
            level.neighborChanged(targetState, targetPos, this, proxyPos, false);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (getMode(state) == PROXY_MODE.READ) return getSignal(state);
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.redstone_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }

    public enum PROXY_MODE implements StringRepresentable {
        READ("read"), WRITE("write");

        private final String name;
        PROXY_MODE(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
