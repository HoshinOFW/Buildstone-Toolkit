package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.PlayerProxyInteractionPayload;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.SetProxyTargetPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BuildstonePackets {
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SetProxyTargetPayload.TYPE,
                SetProxyTargetPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.queue(() -> {
                        if (!(context.getPlayer() instanceof ServerPlayer player)) return;
                        if (!(player.level() instanceof ServerLevel serverLevel)) return;

                        BlockEntity be = serverLevel.getBlockEntity(payload.proxyPos());
                        if (be instanceof ProxyBlockEntity<?> proxyBE) {
                            proxyBE.setLinkedAbsPos(payload.targetPos());
                        }
                    });
                }
        );
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PlayerProxyInteractionPayload.TYPE,
                PlayerProxyInteractionPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (!(context.getPlayer() instanceof ServerPlayer player)) return;
                    if (!(player.level() instanceof ServerLevel serverLevel)) return;

                    for (BlockPos proxyPos : payload.proxyPos()) {
                        BlockState proxyState = serverLevel.getBlockState(proxyPos);
                        if (proxyState.getBlock() instanceof InteractiveProxyBlock<?> interactiveProxyBlock) {
                            interactiveProxyBlock.handleInteraction(serverLevel, proxyPos, proxyState, payload.playerPosition(), payload.interactionType());
                        }
                    }
                }
        );
    }

}
