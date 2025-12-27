package com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.CodecUtil;
import com.github.hoshinofw.multiversion.Overwrite;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
@Overwrite
public record PlayerProxyInteractionPacket(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType)
implements CustomPacketPayload{

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

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType) {
        NetworkManager.sendToServer(new PlayerProxyInteractionPacket(proxyPos, playerPosition, interactionType));
    }

}
