package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.ProxyInteractionType;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;


public record PlayerProxyInteractionPacket(long[] proxyPos, Vec3 playerPosition, ProxyInteractionType[] interactionTypes){
    public static final ResourceLocation ID = BuildstoneToolkit.RLFromPath("proxy_interaction_packet");

    public static final Handler HANDLER = new Handler();

    public static class Handler extends C2SPacketHandler<PlayerProxyInteractionPacket> {

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, PlayerProxyInteractionPacket packet) {
            buf.writeLongArray(packet.proxyPos);
            buf.writeVector3f(packet.playerPosition.toVector3f());
            int[] array = new int[packet.interactionTypes.length];
            int index = 0;
            for (ProxyInteractionType type : (packet.interactionTypes)) {
                array[index] = type.getIndex();
                index++;
            }
            buf.writeVarIntArray(array);
            return buf;
        }

        @Override
        public PlayerProxyInteractionPacket read(FriendlyByteBuf buf) {
            long[] proxyPos = buf.readLongArray();
            Vec3 playerPosition = new Vec3(buf.readVector3f());
            int[] array = buf.readVarIntArray();
            int index = 0;
            ProxyInteractionType[] iTypeArray = new ProxyInteractionType[array.length];
            for (int typeIndex : array) {
                iTypeArray[index] = ProxyInteractionType.of(typeIndex);
                index++;
            }
            return new PlayerProxyInteractionPacket(proxyPos, playerPosition, iTypeArray);
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        protected void handlePacket(NetworkManager.PacketContext context, PlayerProxyInteractionPacket payload) {
            if (!(context.getPlayer() instanceof ServerPlayer player)) return;
            if (!(player.level() instanceof ServerLevel serverLevel)) return;
            for (long l : payload.proxyPos()) {
                BlockPos proxyPos = BlockPos.of(l);
                BlockState proxyState = serverLevel.getBlockState(proxyPos);
                if (proxyState.getBlock() instanceof InteractiveProxyBlock interactiveProxyBlock) {
                    interactiveProxyBlock.handleInteraction(serverLevel, proxyPos, proxyState, context.getPlayer().position(), payload.interactionTypes());
                }
            }
        }
    }

}
