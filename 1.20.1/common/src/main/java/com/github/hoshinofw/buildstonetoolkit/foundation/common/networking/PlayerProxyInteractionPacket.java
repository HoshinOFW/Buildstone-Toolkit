package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record PlayerProxyInteractionPacket(Collection<BlockPos> proxyPos, Vec3 playerPosition, ProxyInteractionType interactionType) {
    public static final ResourceLocation ID =
            new ResourceLocation(BuildstoneToolkit.MOD_ID, "proxy_interaction");

    public static void write(FriendlyByteBuf buf, @NotNull Collection<BlockPos> proxyPos, @NotNull Vec3 playerPosition, ProxyInteractionType interactionType) {
        buf.writeLongArray(Util.buildLongArray(proxyPos));
        buf.writeVector3f(playerPosition.toVector3f());
        buf.writeInt(interactionType.getIndex());
    }

    public static PlayerProxyInteractionPacket read(FriendlyByteBuf buf) {
        Collection<BlockPos> proxyPosCollection = Util.buildBlockPosList(buf.readLongArray());
        Vec3 playerPosition = new Vec3(buf.readVector3f());
        ProxyInteractionType proxyInteractionType = ProxyInteractionType.of(buf.readInt());
        return new PlayerProxyInteractionPacket(proxyPosCollection, playerPosition, proxyInteractionType);
    }

    public static void sendToServer(@NotNull Collection<BlockPos> proxyPosCollection, @NotNull Vec3 playerPosition, ProxyInteractionType interactionType) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf, proxyPosCollection, playerPosition, interactionType);
        NetworkManager.sendToServer(ID, buf);
    }

    public static void register() {

    }
}
