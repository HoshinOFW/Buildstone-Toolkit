package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.LookingAtProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.RegisteredProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.ProxyInteractionType;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class LookingAtProxyBlock extends RegisteredProxyBlock<LookingAtProxyBlockEntity> implements InteractiveProxyBlock<LookingAtProxyBlock> {
    public static final int maxTick = 1;

    public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power_level", 0, 15);
    public static final IntegerProperty TICK = IntegerProperty.create("tick", 0, maxTick);

    private static final Map<ServerLevel, ProxyRegistry<LookingAtProxyBlockEntity>> serverRegistryMap = new Object2ObjectOpenHashMap<>();
    private static final Map<ClientLevel, ProxyRegistry<LookingAtProxyBlockEntity>> clientRegistryMap = new Object2ObjectOpenHashMap<>();

    public LookingAtProxyBlock(Properties properties) {
        super(properties, LookingAtProxyBlockEntity.class);
        this.registerDefaultState(this.defaultBlockState().setValue(POWER_LEVEL, 0).setValue(TICK, 0));
    }

    public static void setSignal(Level level, BlockState state , BlockPos proxyPos, int newSignal) {
        if (newSignal >= 0 && newSignal <= 15 && state.getBlock() instanceof LookingAtProxyBlock) {
            level.setBlock(proxyPos, state.setValue(POWER_LEVEL, newSignal), Block.UPDATE_ALL);
        }
    }

    public static BlockState setSignal(BlockState state, int newSignal) {
        if (newSignal >= 0 && newSignal <= 15 && state.getBlock() instanceof LookingAtProxyBlock) {
            return state.setValue(POWER_LEVEL, newSignal);
        }
        return state;
    }

    public static int getSignal(BlockState state) {
        if (state.getBlock() instanceof LookingAtProxyBlock) {
            return state.getValue(POWER_LEVEL);
        }
        return 0;
    }

    public static int getTick(BlockState state) {
        if (state.getBlock() instanceof LookingAtProxyBlock) {
            return state.getValue(TICK);
        }
        return 0;
    }

    public static int getTick(Level level, BlockPos proxyPos) {
        return getTick(level.getBlockState(proxyPos));
    }

    public static BlockState tickDown(BlockState state) {
        if (state.getBlock() instanceof LookingAtProxyBlock) {
            int newTick = getTick(state) - 1;
            int safeNewTick = Math.clamp(newTick, 0, maxTick);
            return state.setValue(TICK, safeNewTick);
        }
        return state;
    }

    public static void tickDown(Level level, BlockPos proxyPos) {
        level.setBlock(proxyPos, tickDown(level.getBlockState(proxyPos)), Block.UPDATE_NONE);
    }

    public static void endTicking(Level level, BlockPos proxyPos, BlockState proxyState) {
        setSignal(level, proxyState, proxyPos, 0);
    }

    public static BlockState refreshTicking(BlockState state) {
        if (state.getBlock() instanceof LookingAtProxyBlock) {
            return state.setValue(TICK, 0);
        }
        return state;
    }

    public static void startTicking(Level level, BlockPos proxyPos, BlockState proxyState, Vec3 playerPosition) {;
        level.setBlock(proxyPos, setSignal(refreshTicking(proxyState), Util.calculateSignal(playerPosition.distanceTo(proxyPos.getCenter()))), Block.UPDATE_ALL);
        level.scheduleTick(proxyPos, getBlock(), Block.UPDATE_NONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER_LEVEL, TICK);
    }

    public static LookingAtProxyBlock getBlock() {
        return BuildstoneBlocks.LOOKING_AT_PROXY.get();
    }

    @Override
    protected @NotNull Map<ClientLevel, ProxyRegistry<LookingAtProxyBlockEntity>> getClientRegistryMap() {
        return clientRegistryMap;
    }

    @Override
    protected @NotNull Map<ServerLevel, ProxyRegistry<LookingAtProxyBlockEntity>> getServerRegistryMap() {
        return serverRegistryMap;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LookingAtProxyBlockEntity(blockPos, blockState);
    }

    @Override
    protected boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return getSignal(blockState);
    }

    @Override
    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        int tick = getTick(blockState);
        if (tick == 0) {
            endTicking(serverLevel, blockPos, blockState);
        }
        else {
            tickDown(serverLevel, blockPos);
            serverLevel.scheduleTick(blockPos, blockState.getBlock(), Block.UPDATE_NONE);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.looking_at_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }

    @Override
    public void handleInteraction(ServerLevel level, BlockPos proxyPos, BlockState proxyState, Vec3 playerPosition, ProxyInteractionType interactionType) {
        switch (interactionType) {
            case LookedAtLookingAtProxy -> startTicking(level, proxyPos, proxyState, playerPosition);
            case StoppedLookingAtLookingAtProxy -> setSignal(level, proxyState, proxyPos, 0);
            default -> {}
        }
    }
}
