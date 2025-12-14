package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import net.minecraft.core.BlockPos;

public class UpdateUtil {

    public static void doTargetUpdatedLogic(BlockPos targetPos) {
        if (RedstoneProxyBlockEntity.getRegistryStatic().isTargeted(targetPos)) {
            for (RedstoneProxyBlockEntity be : RedstoneProxyBlockEntity.getRegistryStatic().getProxiesTargeting(targetPos)) {
                be.getBlock().targetUpdated(be);
            }
        }
    }

}
