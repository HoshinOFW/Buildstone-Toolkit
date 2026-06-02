package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SableUtil {

    public static void targetMovedBulk(ServerLevel originLevel, SubLevelAssemblyHelper.AssemblyTransform transform, Iterable<BlockPos> blocks) {
        ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(originLevel);
        registry.targetIndex.forEachInIfPresent(blocks, oldPosLong -> {
            BlockPos oldPos = BlockPos.of(oldPosLong);
            BlockPos newPos = transform.apply(oldPos);
            if (registry.targetIdRegistry != null) {
                registry.targetIdRegistry.retarget(oldPosLong, newPos.asLong());
            }
            for (ProxyBlockEntity<?, ?> pbe : registry.getProxiesTargeting(oldPos)) {
                pbe.relocateTargetPos(newPos);
            }
        });
    }

    public static void proxyAssembled(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        if (!(newState.getBlock() instanceof ProxyBlock<?, ?>)) return;
        if (!(originLevel.getBlockEntity(oldPos) instanceof ProxyBlockEntity<?, ?> source)) return;
        if (!(resultingLevel.getBlockEntity(newPos) instanceof ProxyBlockEntity<?, ?> dest)) return;
        if (source == dest) return;
        dest.adoptIdFrom(source);
    }
}
