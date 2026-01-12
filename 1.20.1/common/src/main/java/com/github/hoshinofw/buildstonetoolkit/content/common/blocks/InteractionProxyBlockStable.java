package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.InteractionProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.InteractionProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.RegisteredProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable.ProxyInteractionType;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.ProxyRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class InteractionProxyBlockStable extends RegisteredProxyBlock<InteractionProxyBlockEntity> implements InteractiveProxyBlock<InteractionProxyBlock> {
    public static final int maxTick = 2;

    public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power_level", 0, 15);
    public static final IntegerProperty TICK = IntegerProperty.create("tick", 0, maxTick);

    private static final Map<Level, ProxyRegistry<InteractionProxyBlockEntity>> serverRegistryMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Level, ProxyRegistry<InteractionProxyBlockEntity>> clientRegistryMap = new Object2ObjectOpenHashMap<>();

    public InteractionProxyBlockStable(Properties properties) {
        super(properties, InteractionProxyBlockEntity.class);
    }

    public static void setSignal(Level level, BlockState state , BlockPos proxyPos, int newSignal) {
        if (newSignal >= 0 && newSignal <= 15 && state.getBlock() instanceof InteractionProxyBlock) {
            level.setBlock(proxyPos, state.setValue(POWER_LEVEL, newSignal), Block.UPDATE_ALL);
        }
    }

    public static BlockState setSignal(BlockState state, int newSignal) {
        if (newSignal >= 0 && newSignal <= 15 && state.getBlock() instanceof InteractionProxyBlock) {
            return state.setValue(POWER_LEVEL, newSignal);
        }
        return state;
    }

    public static int getSignal(BlockState state) {
        if (state.getBlock() instanceof InteractionProxyBlock) {
            return state.getValue(POWER_LEVEL);
        }
        return 0;
    }

    public static int getTick(BlockState state) {
        if (state.getBlock() instanceof InteractionProxyBlock) {
            return state.getValue(TICK);
        }
        return 0;
    }

    public static int getTick(Level level, BlockPos proxyPos) {
        return getTick(level.getBlockState(proxyPos));
    }


    public static BlockState refreshTicking(BlockState state) {
        if (state.getBlock() instanceof InteractionProxyBlock) {
            return state.setValue(TICK, 2);
        }
        return state;
    }
    

    public static void startTicking(Level level, BlockPos proxyPos, BlockState proxyState, Vec3 playerPosition) {;
        level.setBlock(proxyPos, setSignal(refreshTicking(proxyState), Util.calculateSignal(playerPosition.distanceTo(proxyPos.getCenter()))), Block.UPDATE_ALL);
        level.scheduleTick(proxyPos, getBlock(), 1);
    }

    public static BlockState tickDown(BlockState state) {
        if (state.getBlock() instanceof InteractionProxyBlock) {
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
    public void handleInteraction(ServerLevel level, BlockPos proxyPos, BlockState proxyState, Vec3 playerPosition, ProxyInteractionType interactionType) {
        switch (interactionType) {
            case RightClickedRightClickProxy -> startTicking(level, proxyPos, proxyState, playerPosition);
            case StoppedRightClickingRightClockProxy -> setSignal(level, proxyState, proxyPos, 0);
            default -> {}
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER_LEVEL, TICK);
    }

    public static InteractionProxyBlock getBlock() {
        return BuildstoneBlocks.INTERACTION_PROXY.get();
    }

    @Override
    protected @NotNull Map<Level, ProxyRegistry<InteractionProxyBlockEntity>> getClientRegistryMap() {
        return clientRegistryMap;
    }

    @Override
    protected @NotNull Map<Level, ProxyRegistry<InteractionProxyBlockEntity>> getServerRegistryMap() {
        return serverRegistryMap;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new InteractionProxyBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return getSignal(blockState);
    }


}