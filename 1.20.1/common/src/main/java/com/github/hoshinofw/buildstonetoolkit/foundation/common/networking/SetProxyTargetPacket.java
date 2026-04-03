package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos) {

    public static final ResourceLocation ID =
            new ResourceLocation(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    // Encode to buffer
    public static void write(FriendlyByteBuf buf, @NotNull BlockPos proxyPos, @NotNull BlockPos targetPos) {
        buf.writeBlockPos(proxyPos);
        buf.writeBlockPos(targetPos);
    }

    // Decode from buffer
    public static SetProxyTargetPacket read(FriendlyByteBuf buf) {
        BlockPos proxyPos = buf.readBlockPos();
        BlockPos targetPos = buf.readBlockPos();
        return new SetProxyTargetPacket(proxyPos, targetPos);
    }

    // Optional helper for client-side sending
    public static void sendToServer(BlockPos proxyPos, BlockPos targetPos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf, proxyPos, targetPos);
        NetworkManager.sendToServer(ID, buf);
    }

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SetProxyTargetPacket.ID,
                (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
                    SetProxyTargetPacket payload = SetProxyTargetPacket.read(buf);

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

}
