package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.OverwriteTypeDeclaration;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@DeleteMethodsAndFields({"read", "write"})
@OverwriteTypeDeclaration
public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos, TargetFace face) implements CustomPacketPayload {

    @OverwriteVersion
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    public static final Type<SetProxyTargetPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetProxyTargetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetProxyTargetPacket::proxyPos,
                    BlockPos.STREAM_CODEC, SetProxyTargetPacket::targetPos,
                    TargetFace.STREAM_CODEC, SetProxyTargetPacket::face,
                    SetProxyTargetPacket::new
            );

    @OverwriteVersion
    public static void sendToServer(BlockPos proxyPos, BlockPos targetPos, TargetFace face) {
        NetworkManager.sendToServer(new SetProxyTargetPacket(proxyPos, targetPos, face));
    }

    @ShadowVersion
    public static void handlePacket(SetProxyTargetPacket payload, NetworkManager.PacketContext context);

    @OverwriteVersion
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SetProxyTargetPacket.TYPE,
                SetProxyTargetPacket.STREAM_CODEC,
                (SetProxyTargetPacket payload, NetworkManager.PacketContext context) -> {
                    context.queue(() -> handlePacket(payload, context));
                }
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}