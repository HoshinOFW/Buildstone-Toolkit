package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.CodecUtil;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@DeleteMethodsAndFields({"read", "write"})
public record PlayerProxyInteractionPacket(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType) implements CustomPacketPayload{

    @OverwriteVersion
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "proxy_interaction");

    public static final CustomPacketPayload.Type<PlayerProxyInteractionPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerProxyInteractionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    CodecUtil.POSCOLLECTION_STREAM_CODEC, PlayerProxyInteractionPacket::proxyPos,
                    CodecUtil.VEC3_STREAM_CODEC, PlayerProxyInteractionPacket::playerPosition,
                    ProxyInteractionType.STREAM_CODEC, PlayerProxyInteractionPacket::interactionType,
                    PlayerProxyInteractionPacket::new
            );

    @OverwriteVersion
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
                            interactiveProxyBlock.handleInteraction(serverLevel, proxyPos, proxyState, context.getPlayer().position(), payload.interactionType());
                        }
                    }
                }
        );
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OverwriteVersion
    public static void sendToServer(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType) {
        NetworkManager.sendToServer(new PlayerProxyInteractionPacket(proxyPos, playerPosition, interactionType));
    }

}
