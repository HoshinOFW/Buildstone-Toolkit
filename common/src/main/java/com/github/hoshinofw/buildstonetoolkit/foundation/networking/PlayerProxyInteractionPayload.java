package com.github.hoshinofw.buildstonetoolkit.foundation.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongArrays;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record PlayerProxyInteractionPayload(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType)
implements CustomPacketPayload{

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "proxy_interaction");

    public static final CustomPacketPayload.Type<PlayerProxyInteractionPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerProxyInteractionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    Util.POSCOLLECTION_STREAM_CODEC, PlayerProxyInteractionPayload::proxyPos,
                    Util.VEC3_STREAM_CODEC, PlayerProxyInteractionPayload::playerPosition,
                    ProxyInteractionType.STREAM_CODEC, PlayerProxyInteractionPayload::interactionType,
                    PlayerProxyInteractionPayload::new
            );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType) {
        NetworkManager.sendToServer(new PlayerProxyInteractionPayload(proxyPos, playerPosition, interactionType));
    }

}
