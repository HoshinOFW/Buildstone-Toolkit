package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.OverwriteTypeDeclaration;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

@DeleteMethodsAndFields({"read", "write"})
@OverwriteTypeDeclaration
public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos)
        implements CustomPacketPayload {

    @OverwriteVersion
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    public static final Type<SetProxyTargetPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetProxyTargetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetProxyTargetPacket::proxyPos,
                    BlockPos.STREAM_CODEC, SetProxyTargetPacket::targetPos,
                    SetProxyTargetPacket::new
            );

    @OverwriteVersion
    public static void sendToServer(BlockPos proxyPos, BlockPos targetPos) {
        NetworkManager.sendToServer(new SetProxyTargetPacket(proxyPos, targetPos));
    }

    @OverwriteVersion
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

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}