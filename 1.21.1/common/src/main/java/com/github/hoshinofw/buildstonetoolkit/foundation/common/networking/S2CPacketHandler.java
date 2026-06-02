package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class S2CPacketHandler<P> {

    protected StreamCodec<RegistryFriendlyByteBuf, Payload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Payload decode(RegistryFriendlyByteBuf buf) {
            return new Payload(read(buf));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, Payload packet) {
            write(buf, packet.packet);
        }
    };

    protected CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(getID());

    @ShadowVersion
    public abstract ResourceLocation getID();

    @ShadowVersion
    protected abstract FriendlyByteBuf write(FriendlyByteBuf buf, P packet);

    @ShadowVersion
    protected abstract P read(FriendlyByteBuf buf);

    @ShadowVersion
    protected abstract void handlePacket(NetworkManager.PacketContext context, P packet);

    @OverwriteVersion
    public void sendToPlayer(ServerPlayer player, P packet) {
        NetworkManager.sendToPlayer(player, new Payload(packet));
    }

    @OverwriteVersion
    public void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                TYPE,
                STREAM_CODEC,
                (payload, context) -> {
                    context.queue(() -> handlePacket(context, payload.packet));
                }
        );
    }

    public class Payload implements CustomPacketPayload {

        protected P packet;

        protected Payload(P packet) {
            this.packet = packet;
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public P get() {
            return packet;
        }
    }
}
