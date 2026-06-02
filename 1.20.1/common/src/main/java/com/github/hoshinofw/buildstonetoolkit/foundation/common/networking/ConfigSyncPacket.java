package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.config.BTConfig;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record ConfigSyncPacket (int interactionProxyRayReach, int proxyTunerRayReach, List<TagKey<Block>> interactionProxyRaySkip) {

    public static ResourceLocation ID = BuildstoneToolkit.RLFromPath("config_sync_packet");

    public static void sendToPlayer(ServerPlayer player) {
        sendToPlayer(player, BTConfig.getInteractionProxyRayReach(), BTConfig.getProxyTunerRayReach(), BTConfig.getInteractionProxyRaySkip());
    }

    public static void sendToPlayer(ServerPlayer player, int interactionProxyRayReach, int proxyTunerRayReach, List<TagKey<Block>> interactionProxyRaySkip) {
        NetworkManager.sendToPlayer(player, ID, write(new FriendlyByteBuf(Unpooled.buffer()),
                interactionProxyRayReach, proxyTunerRayReach, interactionProxyRaySkip));
    }

    public static FriendlyByteBuf write(FriendlyByteBuf buf, int interactionProxyRayReach, int proxyTunerRayReach, List<TagKey<Block>> interactionProxyRaySkip) {
        buf.writeInt(interactionProxyRayReach);
        buf.writeInt(proxyTunerRayReach);
        buf.writeVarInt(interactionProxyRaySkip.size());
        for (TagKey<Block> tag : interactionProxyRaySkip) {
            buf.writeResourceLocation(tag.location());
        }
        return buf;
    }

    public static ConfigSyncPacket read(FriendlyByteBuf buf)  {
        int interactionProxyRayReach = buf.readInt();
        int proxyTunerRayReach = buf.readInt();
        int size = buf.readVarInt();
        ObjectArrayList<TagKey<Block>> interactionProxyRaySkip = new ObjectArrayList<>(size);
        for (int i = 0; i < size; i++) {
            interactionProxyRaySkip.add(TagKey.create(Registries.BLOCK, buf.readResourceLocation()));
        }
        return new ConfigSyncPacket(interactionProxyRayReach, proxyTunerRayReach, interactionProxyRaySkip);
    }

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C, ID,
                (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
                    ConfigSyncPacket payload = read(buf);
                    context.queue(() -> handlePacket(payload, context));
                });
    }

    public static void handlePacket(ConfigSyncPacket payload, NetworkManager.PacketContext context) {
        BTConfig.setInteractionProxyRayReach(payload.interactionProxyRayReach);
        BTConfig.setProxyTunerRayReach(payload.proxyTunerRayReach);
        BTConfig.setInteractionProxyRaySkip(payload.interactionProxyRaySkip);
    }
}
