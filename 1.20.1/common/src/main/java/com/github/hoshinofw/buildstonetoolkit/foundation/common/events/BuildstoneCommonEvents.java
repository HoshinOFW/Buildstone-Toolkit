package com.github.hoshinofw.buildstonetoolkit.foundation.common.events;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.RPTransitionParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.ConfigSyncPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BuildstoneCommonEvents {

    public static void register() {
        LifecycleEvent.SETUP.register(BuildstoneToolkit::postInit);
        LifecycleEvent.SERVER_LEVEL_LOAD.register(BuildstoneToolkit::onServerLoad);
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(BuildstoneToolkit::onServerUnload);
        PlayerEvent.PLAYER_JOIN.register(BuildstoneCommonEvents::onServerJoin);
    }

    private static void onServerJoin(ServerPlayer serverPlayer) {
        ConfigSyncPacket.HANDLER.sendToPlayer(serverPlayer);
    }

}
