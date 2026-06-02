package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class S2CPacketHandler<P> {

    public abstract ResourceLocation getID();

    protected abstract FriendlyByteBuf write(FriendlyByteBuf buf, P payload);

    protected abstract P read(FriendlyByteBuf buf);

    protected abstract void handlePacket(NetworkManager.PacketContext context, P payload);

    public void sendToPlayer(ServerPlayer player, P payload) {
        NetworkManager.sendToPlayer(player, getID(), write(new FriendlyByteBuf(Unpooled.buffer()), payload));
    }

    public void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                getID(),
                (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
                    P payload = read(buf);
                    context.queue(() -> handlePacket(context, payload));
                });
    }

}
