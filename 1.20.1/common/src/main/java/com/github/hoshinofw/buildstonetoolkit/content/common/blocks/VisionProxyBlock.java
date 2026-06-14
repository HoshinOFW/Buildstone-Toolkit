package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.VisionProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.ProxyInteractionType;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VisionProxyBlock extends ProxyBlock<VisionProxyBlock, VisionProxyBlockEntity> implements InteractiveProxyBlock {
    public static final int maxTick = 2;

    public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power_level", 0, 15);
    public static final IntegerProperty TICK = IntegerProperty.create("tick", 0, maxTick);


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER_LEVEL, TICK);
    }

    public VisionProxyBlock(Properties properties) {
        super(properties, VisionProxyBlockEntity.class);
        this.registerDefaultState(this.defaultBlockState().setValue(POWER_LEVEL, 0).setValue(TICK, 0));
    }

    public static void setSignal(Level level, BlockState state, BlockPos proxyPos, int newSignal) {
        if (newSignal >= 0 && newSignal <= 15 && state.getBlock() instanceof VisionProxyBlock) {
            level.setBlock(proxyPos, state.setValue(POWER_LEVEL, newSignal), Block.UPDATE_ALL);
        }
    }

    public static BlockState setSignal(BlockState state, int newSignal) {
        if (newSignal >= 0 && newSignal <= 15 && state.getBlock() instanceof VisionProxyBlock) {
            return state.setValue(POWER_LEVEL, newSignal);
        }
        return state;
    }

    public static int getSignal(BlockState state) {
        if (state.getBlock() instanceof VisionProxyBlock) {
            return state.getValue(POWER_LEVEL);
        }
        return 0;
    }

    public static int getTick(BlockState state) {
        if (state.getBlock() instanceof VisionProxyBlock) {
            return state.getValue(TICK);
        }
        return 0;
    }

    public static BlockState refreshTicking(BlockState state) {
        if (state.getBlock() instanceof VisionProxyBlock) {
            return state.setValue(TICK, maxTick);
        }
        return state;
    }

    public static void startTicking(Level level, BlockPos proxyPos, BlockState proxyState, Vec3 playerPosition) {
        level.setBlock(proxyPos, setSignal(refreshTicking(proxyState), InteractiveProxyBlock.calculateSignal(playerPosition.distanceTo(proxyPos.getCenter()))), Block.UPDATE_ALL);
        level.scheduleTick(proxyPos, getBlock(), 1);
    }

    public static BlockState tickDown(BlockState state) {
        if (state.getBlock() instanceof VisionProxyBlock) {
            int newTick = getTick(state) - 1;
            if (newTick > maxTick) newTick = maxTick;
            if (newTick < 0) newTick = 0;
            return state.setValue(TICK, newTick);
        }
        return state;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        BlockState newState = setSignal(tickDown(blockState), 0);
        serverLevel.setBlock(blockPos, newState, Block.UPDATE_ALL);

        if (getTick(newState) != 0) {
            serverLevel.scheduleTick(blockPos, newState.getBlock(), 1);
        }
    }

    @Override
    public void handleInteraction(ServerLevel level, BlockPos proxyPos, BlockState proxyState, Vec3 playerPosition, ProxyInteractionType[] interactionTypes) {
        if (!isActive(proxyState)) return;
        for (ProxyInteractionType interactionType : interactionTypes) {
            switch (interactionType) {
                case LookedAtLookingAtProxy -> startTicking(level, proxyPos, proxyState, playerPosition);
                case StoppedLookingAtLookingAtProxy -> setSignal(level, proxyState, proxyPos, 0);
                default -> {
                }
            }
        }
    }

    public static VisionProxyBlock getBlock() {
        return BuildstoneBlocks.VISION_PROXY.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new VisionProxyBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return getSignal(blockState);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.vision_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }
}
