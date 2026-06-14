package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable;


import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** See ProxyBlockMixin, where this interface is injected into ProxyBlock if sable is present.*/
public interface ProxyMoveListener extends BlockSubLevelAssemblyListener {

    @Override
    default void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        SableUtil.proxyAssembled(originLevel, resultingLevel, newState, oldPos, newPos);
    }
}
