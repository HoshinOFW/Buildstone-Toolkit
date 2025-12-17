package com.github.hoshinofw.buildstonetoolkit.foundation.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SetProxyTargetPayload(BlockPos proxyPos, BlockPos targetPos)
        implements CustomPacketPayload {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    public static final Type<SetProxyTargetPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SetProxyTargetPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetProxyTargetPayload::proxyPos,
                    BlockPos.STREAM_CODEC, SetProxyTargetPayload::targetPos,
                    SetProxyTargetPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}