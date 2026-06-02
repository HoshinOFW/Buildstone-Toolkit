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

    private static int LAST_TICK = 0;
    private static final int LCB_COOLDOWN = 4;

    public static void register() {
        LifecycleEvent.SETUP.register(BuildstoneToolkit::postInit);
        LifecycleEvent.SERVER_LEVEL_LOAD.register(BuildstoneToolkit::onServerLoad);
        PlayerEvent.PLAYER_JOIN.register(BuildstoneCommonEvents::onServerJoin);
        InteractionEvent.LEFT_CLICK_BLOCK.register(BuildstoneCommonEvents::onLeftClickBlock);
    }

    private static void onServerJoin(ServerPlayer serverPlayer) {
        ConfigSyncPacket.HANDLER.sendToPlayer(serverPlayer);
    }

    //TODO MAKE A packet
    private static EventResult onLeftClickBlock(Player player, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {

        Level level = player.level();
        ItemStack item = player.getItemInHand(interactionHand);
        if (item.is(BuildstoneItems.MOD_WAND.get())) {
            BlockState state = level.getBlockState(blockPos);
            if (state.getBlock() instanceof RedstoneProxyBlock rpb) {
                if (rpb.hasProxyBlockEntity(level, blockPos)) {
                    long proxyId = rpb.getId(level, blockPos);
                    RedstoneProxyBlock.cycleMode(level, blockPos, state);
                    if (level.isClientSide()) {
                        player.playSound(SoundEvents.STONE_BUTTON_CLICK_ON, 1f, 0.2f);
                        RPTransitionParticle.spawn(player, blockPos, proxyId);
                    }
                    return EventResult.interruptTrue();
                }
            }
        }

        return EventResult.pass();
    }

}
