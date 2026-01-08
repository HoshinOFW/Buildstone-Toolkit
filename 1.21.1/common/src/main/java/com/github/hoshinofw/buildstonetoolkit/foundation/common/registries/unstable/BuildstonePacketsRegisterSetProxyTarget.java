package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable.SetProxyTargetPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BuildstonePacketsRegisterSetProxyTarget {

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SetProxyTargetPacket.TYPE,
                SetProxyTargetPacket.STREAM_CODEC,
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
    }

}
