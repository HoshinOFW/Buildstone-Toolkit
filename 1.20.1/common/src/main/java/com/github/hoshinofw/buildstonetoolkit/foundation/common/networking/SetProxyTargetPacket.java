package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;


public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos, TargetFace face) {

    public static final ResourceLocation ID =
            new ResourceLocation(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    public static void write(FriendlyByteBuf buf, BlockPos proxyPos, BlockPos targetPos, TargetFace face) {
        buf.writeBlockPos(proxyPos);
        buf.writeBlockPos(targetPos);
        buf.writeInt(face.getIndex());
    }

    public static SetProxyTargetPacket read(FriendlyByteBuf buf) {
        BlockPos proxyPos = buf.readBlockPos();
        BlockPos targetPos = buf.readBlockPos();
        TargetFace face = TargetFace.fromIndex(buf.readInt());
        return new SetProxyTargetPacket(proxyPos, targetPos, face);
    }

    public static void sendToServer(BlockPos proxyPos, BlockPos targetPos) {
        sendToServer(proxyPos, targetPos, TargetFace.ALL);
    }

    public static void sendToServer(BlockPos proxyPos, BlockPos targetPos, TargetFace face) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf, proxyPos, targetPos, face);
        NetworkManager.sendToServer(ID, buf);
    }

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SetProxyTargetPacket.ID,
                (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
                    SetProxyTargetPacket payload = SetProxyTargetPacket.read(buf);
                    context.queue(() -> SetProxyTargetPacket.handlePacket(payload, context));
                }
        );
    }

    public static void handlePacket(SetProxyTargetPacket payload, NetworkManager.PacketContext context) {
        if (!(context.getPlayer() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        BlockState state = serverLevel.getBlockState(payload.proxyPos);
        if (state.getBlock() instanceof ProxyBlock<?,?> pb) {
            pb.setLinkedAbsPos(serverLevel, payload.proxyPos, payload.targetPos);
        }
        if (state.getBlock() instanceof FaceTargetingProxyBlock ftpb) {
            ftpb.setTargetFace(serverLevel, payload.proxyPos, state, payload.face);
        }
    }

}
