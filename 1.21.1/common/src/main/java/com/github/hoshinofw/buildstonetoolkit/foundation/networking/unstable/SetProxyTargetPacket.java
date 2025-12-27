package com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.Overwrite;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Overwrite
public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos)
        implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    public static final Type<com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.SetProxyTargetPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.SetProxyTargetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.SetProxyTargetPacket::proxyPos,
                    BlockPos.STREAM_CODEC, com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.SetProxyTargetPacket::targetPos,
                    com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.SetProxyTargetPacket::new
            );

    public static void sendToServer(BlockPos proxyPos, BlockPos targetPos) {
        NetworkManager.sendToServer(new com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.SetProxyTargetPacket(proxyPos, targetPos));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}