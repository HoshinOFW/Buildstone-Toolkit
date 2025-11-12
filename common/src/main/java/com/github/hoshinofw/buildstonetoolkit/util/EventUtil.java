package com.github.hoshinofw.buildstonetoolkit.util;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.IProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;

import static com.github.hoshinofw.buildstonetoolkit.util.Util.*;

public class EventUtil {
    public static void rightClickWandOnProxy(Player player, IProxyBlock proxyBlock, Level level, BlockPos hitPos) {
        BlockPos playerStoredTargetPos = getSelectedPos(player);
        if (playerStoredTargetPos != null) {
            if (proxyBlock.setLinkedAbsPos(level, hitPos, playerStoredTargetPos)) {
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
