package com.github.hoshinofw.buildstonetoolkit.registries.events;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.IProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.util.Util;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import static com.github.hoshinofw.buildstonetoolkit.util.ParticleUtil.*;

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
        Player player = client.player;
        boolean isHovered = player.getMainHandItem().is(BuildstoneItems.MOD_WAND.get());

        if (isHovered && !wasHovered) {
            BlockPos targetPos = Util.getSelectedPos(player);
            if (targetPos != null) {
                if (player.level().getBlockState(targetPos).getBlock() instanceof IProxyBlock) {
                    spawnProxyTargetParticle(player, targetPos);
                    spawnProxyParticle(player, targetPos);
                } else {
                    spawnSelectionParticle(player, targetPos);
                }
            }
        }

        wasHovered = isHovered;
    }
}
