package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.PlayerProxyInteractionPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class BuildstonePacketsRegisterPlayerProxyInteraction {

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PlayerProxyInteractionPacket.TYPE,
                PlayerProxyInteractionPacket.STREAM_CODEC,
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
