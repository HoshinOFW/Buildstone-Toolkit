package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record SetAllayTargetPacket(int allayId, BlockPos targetPos, boolean nullify) implements CustomPacketPayload{

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "set_allay_target");

    public static final CustomPacketPayload.Type<SetAllayTargetPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetAllayTargetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SetAllayTargetPacket::allayId,
                    BlockPos.STREAM_CODEC, SetAllayTargetPacket::targetPos,
                    ByteBufCodecs.BOOL, SetAllayTargetPacket::nullify,
                    SetAllayTargetPacket::new
            );

    public static void sendToServer(int allayId, BlockPos targetPos, boolean nullify) {
        NetworkManager.sendToServer(new SetAllayTargetPacket(allayId, targetPos, nullify));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
