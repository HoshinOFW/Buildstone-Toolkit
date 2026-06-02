package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.OverwriteTypeDeclaration;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import dev.architectury.networking.NetworkManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OverwriteTypeDeclaration
public record ConfigSyncPacket(int interactionProxyRayReach, int proxyTunerRayReach, List<TagKey<Block>> interactionProxyRaySkip) implements CustomPacketPayload {

    @ShadowVersion
    public static ResourceLocation ID;

    public static final CustomPacketPayload.Type<ConfigSyncPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ConfigSyncPacket decode(@NotNull RegistryFriendlyByteBuf buf) {
            int interactionProxyRayReach = buf.readInt();
            int proxyTunerRayReach = buf.readInt();
            int size = buf.readVarInt();
            ObjectArrayList<TagKey<Block>> interactionProxyRaySkip = new ObjectArrayList<>(size);
            for (int i = 0; i < size; i++) {
                interactionProxyRaySkip.add(TagKey.create(Registries.BLOCK, buf.readResourceLocation()));
            }
            return new ConfigSyncPacket(interactionProxyRayReach, proxyTunerRayReach, interactionProxyRaySkip);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull ConfigSyncPacket payload) {
            buf.writeInt(payload.interactionProxyRayReach);
            buf.writeInt(payload.proxyTunerRayReach);
            buf.writeVarInt(payload.interactionProxyRaySkip.size());
            for (TagKey<Block> tag : payload.interactionProxyRaySkip) {
                buf.writeResourceLocation(tag.location());
            }
        }
    };


    @OverwriteVersion
    public static void sendToPlayer(ServerPlayer player, int interactionProxyRayReach, int proxyTunerRayReach, List<TagKey<Block>> interactionProxyRaySkip) {
        NetworkManager.sendToPlayer(player, new ConfigSyncPacket(interactionProxyRayReach, proxyTunerRayReach, interactionProxyRaySkip));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OverwriteVersion
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                ConfigSyncPacket.TYPE,
                ConfigSyncPacket.STREAM_CODEC,
                (payload, context) -> {
                    context.queue(() -> handlePacket(payload, context));

                }
        );
    }

    @ShadowVersion
    public static void handlePacket(ConfigSyncPacket payload, NetworkManager.PacketContext context);
}
