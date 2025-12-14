package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;

import static com.github.hoshinofw.buildstonetoolkit.foundation.util.Util.*;

public class EventUtil {

    //TODO REWRITE ALL TO ALLOW MULTIPLE

    public static void rightClickWandOnProxy(Player player, ProxyBlock proxyBlock, Level level, BlockPos hitPos) {
        BlockPos playerStoredTargetPos = getSelectedPos(player);
        if (playerStoredTargetPos != null) {
            if (proxyBlock.setLinkedAbsPos(level, hitPos, playerStoredTargetPos)) { //HERE
                if (level.isClientSide) {
                    SoundUtil.playLinkSuccessSound(player);
                    player.displayClientMessage(Component.translatable("message.buildstonetoolkit.link_success",
                                    blueComponent(level.getBlockState(playerStoredTargetPos).getBlock().getName().getString()),
                                    blueComponent(Arrays.toString(blockPosToArray(playerStoredTargetPos)))),
                            true);
                }
            } else {
                if (level.isClientSide) {
                    SoundUtil.playLinkSuccessSound(player);
                    player.displayClientMessage(Component.translatable("message.buildstonetoolkit.link_fail",
                                    blueComponent(level.getBlockState(playerStoredTargetPos).getBlock().getName().getString()),
                                    blueComponent(Arrays.toString(blockPosToArray(playerStoredTargetPos)))),
                            true);
                }
            }
        }
    }
}
