package com.github.hoshinofw.buildstonetoolkit.foundation.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record SetProxyTargetPacket(BlockPos proxyPos, BlockPos targetPos) {

    public static final ResourceLocation ID =
            new ResourceLocation(BuildstoneToolkit.MOD_ID, "set_proxy_target");

    // Encode to buffer
    public static void write(FriendlyByteBuf buf, BlockPos proxyPos, BlockPos targetPos) {
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

}
