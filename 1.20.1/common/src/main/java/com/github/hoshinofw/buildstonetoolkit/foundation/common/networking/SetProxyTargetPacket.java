package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;


public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos, TargetFace face) {
    public static final ResourceLocation ID = BuildstoneToolkit.RLFromPath("set_proxy_target_packet");

    public static final Handler HANDLER = new Handler();

    public static class Handler extends C2SPacketHandler<SetProxyTargetPacket> {

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        protected FriendlyByteBuf write(FriendlyByteBuf buf, SetProxyTargetPacket payload) {
            buf.writeBlockPos(payload.proxyPos);
            buf.writeBlockPos(payload.targetPos);
            buf.writeInt(payload.face.getIndex());
            return buf;
        }

        @Override
        protected SetProxyTargetPacket read(FriendlyByteBuf buf) {
            BlockPos proxyPos = buf.readBlockPos();
            BlockPos targetPos = buf.readBlockPos();
            TargetFace face = TargetFace.fromIndex(buf.readInt());
            return new SetProxyTargetPacket(proxyPos, targetPos, face);
        }

        @Override
        protected void handlePacket(NetworkManager.PacketContext context, SetProxyTargetPacket payload) {
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

}
