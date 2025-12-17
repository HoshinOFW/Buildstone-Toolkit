package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.ParticleUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.PlayerUtil;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public class BuildstoneClientEvents {
    public static boolean wasHovered = false;

    public static boolean debug = false;

    private static int counter;
    public static int servercounter;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(BuildstoneClientEvents::onClientTick);
        TickEvent.SERVER_POST.register(BuildstoneClientEvents::onServerTick);
    }

    private static void onServerTick(MinecraftServer minecraftServer) {
        if (debug) {
            servercounter++;
            if (counter % 50 == 0) {
//                BuildstoneToolkit.LOGGER.info("SERVER: Size of transmitter log map: {}", RedstoneProxyBlockEntity.getRegistryStatic().size());
//                for (ProxyBlockEntity be : RedstoneProxyBlockEntity.getRegistryStatic().getAllProxies()) {
//                    BuildstoneToolkit.LOGGER.info("SERVER: BlockEntity: {}", be);
//                    BuildstoneToolkit.LOGGER.info("SERVER: Targeting Pos: {}", be.getLinkedAbsPos());
//                }
            }
        }
    }

    private static void onClientTick(Minecraft client) {

        if (client.player == null || client.level == null) {
            wasHovered = false;
            return;
        }
        Player player = client.player;
        boolean isHovered = player.getMainHandItem().is(BuildstoneItems.MOD_WAND.get());

        if (debug) {
            counter++;
            if (counter % 50 == 0 && isHovered) {
                BuildstoneToolkit.LOGGER.info("CLIENT: Size of transmitter log map: {}", RedstoneProxyBlockEntity.getRegistry(client.level).size());
                for (ProxyBlockEntity<?> be : RedstoneProxyBlockEntity.getRegistry(client.level).getAllProxies()) {
                    BuildstoneToolkit.LOGGER.info("CLIENT: BlockEntity: {}", be);
                    BuildstoneToolkit.LOGGER.info("CLIENT: Targeting Pos: {}", be.getLinkedAbsPos());
                }
            }
        }

        if (isHovered && !wasHovered) {
            BlockPos targetPos = PlayerUtil.getSelectedPos(player);
            long targetProxyId = PlayerUtil.getSelectedProxyId(player);
            if (targetPos != null && targetProxyId < 0) {
                if (player.level().getBlockState(targetPos).getBlock() instanceof ProxyBlock) {
                    ParticleUtil.spawnProxyTargetParticle(player, targetPos);
                    ParticleUtil.spawnProxyParticle(player, targetPos);
                } else {
                    ParticleUtil.spawnSelectionParticle(player, targetPos);
                }
            } else if (targetProxyId >= 0 && targetPos == null) {
                ParticleUtil.spawnIdProxyTargetParticle(player, targetProxyId);
                ParticleUtil.spawnIdProxyParticle(player, targetProxyId);
            }
        }

        wasHovered = isHovered;
    }
}
