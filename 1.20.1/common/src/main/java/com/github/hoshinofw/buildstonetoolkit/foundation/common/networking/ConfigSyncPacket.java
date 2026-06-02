package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.config.BTConfig;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.networking.NetworkManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record ConfigSyncPacket (int interactionProxyRayReach, int proxyTunerRayReach, List<TagKey<Block>> interactionProxyRaySkip) {

    public static final ResourceLocation ID = BuildstoneToolkit.RLFromPath("config_sync_packet");

    public static final Handler HANDLER = new Handler();

    public static class Handler extends S2CPacketHandler<ConfigSyncPacket> {

        public void sendToPlayer(ServerPlayer player) {
            sendToPlayer(player,
                    new ConfigSyncPacket(BTConfig.getInteractionProxyRayReach(),
                    BTConfig.getProxyTunerRayReach(), BTConfig.getInteractionProxyRaySkip()));
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        protected FriendlyByteBuf write(FriendlyByteBuf buf, ConfigSyncPacket payload) {
            buf.writeInt(payload.interactionProxyRayReach);
            buf.writeInt(payload.proxyTunerRayReach);
            buf.writeVarInt(payload.interactionProxyRaySkip.size());
            for (TagKey<Block> tag : payload.interactionProxyRaySkip) {
                buf.writeResourceLocation(tag.location());
            }
            return buf;
        }

        @Override
        protected ConfigSyncPacket read(FriendlyByteBuf buf) {
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
        protected void handlePacket(NetworkManager.PacketContext context, ConfigSyncPacket payload) {
            BTConfig.setInteractionProxyRayReach(payload.interactionProxyRayReach);
            BTConfig.setProxyTunerRayReach(payload.proxyTunerRayReach);
            BTConfig.setInteractionProxyRaySkip(payload.interactionProxyRaySkip);
        }
    }
}
