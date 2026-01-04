package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.InteractionProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.VisionProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.PlayerProxyInteractionPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.unstable.ProxyInteractionType;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.ParticleUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.PlayerUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import dev.architectury.event.events.client.ClientTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BuildstoneClientEvents {
    public static boolean wasHovered = false;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(BuildstoneClientEvents::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null || client.level == null) {
            wasHovered = false;
            return;
        }
        tunerHoverLogic(client, client.player, client.level);
        interactionProxyLogic(client, client.player, client.level);
    }

    private static void interactionProxyLogic(Minecraft client, @NotNull LocalPlayer player, ClientLevel level) {
        //TODO Merge proxy registries for efficiency. Examine ways to make this whole system even smoother.
        BlockHitResult hitResult = Util.raycastBlockIgnoringReach(player, level, 64);
        if (client.options.keyUse.isDown()) {
            if (InteractionProxyBlockEntity.getRegistry(level).isTargeted(hitResult.getBlockPos())) {
                PlayerProxyInteractionPacket.sendToServer(
                        InteractionProxyBlockEntity.getRegistry(level).getProxiesTargetingPos(hitResult.getBlockPos()),
                        player.position(), ProxyInteractionType.RightClickedRightClickProxy);
            }
        }
        if (VisionProxyBlockEntity.getRegistry(level).isTargeted(hitResult.getBlockPos())) {
            PlayerProxyInteractionPacket.sendToServer(
                    VisionProxyBlockEntity.getRegistry(level).getProxiesTargetingPos(hitResult.getBlockPos()),
                    player.position(), ProxyInteractionType.LookedAtLookingAtProxy);
        }
    }

    private static void tunerHoverLogic(Minecraft client, @NotNull LocalPlayer player, ClientLevel level) {
        boolean isHovered = player.getMainHandItem().is(BuildstoneItems.MOD_WAND.get());

        if (isHovered && !wasHovered) {
            BlockPos targetPos = PlayerUtil.getSelectedPos(player);
            long targetProxyId = PlayerUtil.getSelectedProxyId(player);
            if (targetPos != null && targetProxyId < 0) {
                if (level.getBlockState(targetPos).getBlock() instanceof ProxyBlock) {
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
