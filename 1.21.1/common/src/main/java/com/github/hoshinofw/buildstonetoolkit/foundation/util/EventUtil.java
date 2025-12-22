package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.networking.SetProxyTargetPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Arrays;

import static com.github.hoshinofw.buildstonetoolkit.foundation.util.Util.blockPosToArray;
import static com.github.hoshinofw.buildstonetoolkit.foundation.util.Util.blueComponent;

public class EventUtil {

    public static void rightClickWandOnProxy(Player player, ProxyBlock proxyBlock, Level level, BlockPos hitPos) {
        if (level.isClientSide()) {
            BlockPos targetPos;
            if (PlayerUtil.getSelectedProxyId(player) > 0) {
                ProxyBlockEntity<?> pbe = IdProxyBlockEntity.getIdRegistry(level).getEntry(PlayerUtil.getSelectedProxyId(player));
                if (pbe != null) {
                    targetPos = pbe.getBlockPos();
                } else {
                    targetPos = PlayerUtil.getSelectedPos(player);
                }
            } else {
                targetPos = PlayerUtil.getSelectedPos(player);
            }
            //Server will call setLinkedAbsPos and update the client.
            SetProxyTargetPayload.sendToServer(hitPos, targetPos);

            //SoundUtil.playLinkSuccessSound(player);
            player.displayClientMessage(Component.translatable("message.buildstonetoolkit.link_success",
                            blueComponent(level.getBlockState(targetPos).getBlock().getName().getString()),
                            blueComponent(Arrays.toString(blockPosToArray(targetPos)))),
                    true);

        }
    }
}
