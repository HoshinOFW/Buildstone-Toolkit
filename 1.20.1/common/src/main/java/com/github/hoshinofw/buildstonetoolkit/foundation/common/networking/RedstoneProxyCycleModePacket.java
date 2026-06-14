package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.CompatUtil;
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

public record RedstoneProxyCycleModePacket(long proxyPos) {

    public static final ResourceLocation ID = BuildstoneToolkit.RLFromPath("redsone_proxy_cycle_mode_packet");

    public static final Handler HANDLER = new Handler();

    public static final double RANGE = 12;

    public static class Handler extends C2SPacketHandler<RedstoneProxyCycleModePacket> {

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, RedstoneProxyCycleModePacket packet) {
            buf.writeLong(packet.proxyPos);
            return buf;
        }

        @Override
        public RedstoneProxyCycleModePacket read(FriendlyByteBuf buf) {
            long proxyPos = buf.readLong();
            return new RedstoneProxyCycleModePacket(proxyPos);
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        protected void handlePacket(NetworkManager.PacketContext context, RedstoneProxyCycleModePacket payload) {
            if (!(context.getPlayer() instanceof ServerPlayer player)) return;
            if (!(player.level() instanceof ServerLevel serverLevel)) return;

            BlockPos proxyPos = BlockPos.of(payload.proxyPos);
            if (player.position().distanceTo(CompatUtil.projectOutOfSublevel(serverLevel, proxyPos.getCenter())) > RANGE) return;

            BlockState proxyState = serverLevel.getBlockState(proxyPos);
            if (proxyState.getBlock() instanceof RedstoneProxyBlock) {
                RedstoneProxyBlock.cycleMode(player.level(), proxyPos, proxyState);
            }
        }
    }

}
