package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.ProxyInteractionType;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.CodecUtil;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.OverwriteTypeDeclaration;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@DeleteMethodsAndFields({"read", "write"})
@OverwriteTypeDeclaration
public record PlayerProxyInteractionPacket(long[] proxyPos, Vec3 playerPosition, ProxyInteractionType[] interactionTypes) implements CustomPacketPayload{

    @ShadowVersion
    public static final ResourceLocation ID;

    public static final CustomPacketPayload.Type<PlayerProxyInteractionPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerProxyInteractionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    CodecUtil.LONG_ARRAY_STREAM_CODEC, PlayerProxyInteractionPacket::proxyPos,
                    CodecUtil.VEC3_STREAM_CODEC, PlayerProxyInteractionPacket::playerPosition,
                    ProxyInteractionType.ARRAY_STREAM_CODEC, PlayerProxyInteractionPacket::interactionTypes,
                    PlayerProxyInteractionPacket::new
            );

    @OverwriteVersion
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PlayerProxyInteractionPacket.TYPE,
                PlayerProxyInteractionPacket.STREAM_CODEC,
                (payload, context) -> {
                    context.queue(() -> handlePacket(payload, context));

                }
        );
    }

    @ShadowVersion
    private static void handlePacket(PlayerProxyInteractionPacket payload, NetworkManager.PacketContext context);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OverwriteVersion
    public static void sendToServer(long[] proxyPos, @NotNull Vec3 playerPosition, ProxyInteractionType[] interactionTypes) {
        NetworkManager.sendToServer(new PlayerProxyInteractionPacket(proxyPos, playerPosition, interactionTypes));
    }

}
