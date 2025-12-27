package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;

public class UpdateUtil {

    private static boolean IN_PROXY_UPDATE = false;

    public static void doInstantTargetUpdatedLogic(BlockPos targetPos, Level level) {
        if (IN_PROXY_UPDATE) return;

        IN_PROXY_UPDATE = true;
        try {
            if (UpdateListenerProxyBlockEntity.getRegistry(level).isTargeted(targetPos)) {
                for (UpdateListenerProxyBlockEntity be : UpdateListenerProxyBlockEntity.getRegistry(level).getProxiesTargeting(targetPos)) {
                    if (be.getBlockPos().asLong() != targetPos.asLong()) {
                        be.getBlock().targetUpdated(be, level);
                    }
                }
            }
        } finally {
            IN_PROXY_UPDATE = false;
        }
    }

     public static int getBestSignalFromRedstoneProxies(Collection<RedstoneProxyBlockEntity> rpbeCollection) {
         int best = 0;
         int signal;
         for (RedstoneProxyBlockEntity rpbe : rpbeCollection) {
             signal = rpbe.getSignal();
             if (signal == 15) {
                 return 15;
             }
             if (signal > best) best = signal;
         }
         return best;
     }

     public static boolean hasSignalFromRedstoneProxies(Collection<RedstoneProxyBlockEntity> rpbeCollection) {
        int signal;
        for (RedstoneProxyBlockEntity rpbe : rpbeCollection) {
             signal = rpbe.getSignal();
             if (signal != 0) {
                 return true;
             }
         }
         return false;
     }
}
