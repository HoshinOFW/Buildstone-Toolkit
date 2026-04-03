package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.ProxyInteractionType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface InteractiveProxyBlock<T extends ProxyBlock> {

     void handleInteraction(ServerLevel level, BlockPos proxyPos, BlockState proxyState,
                                  Vec3 playerPosition, ProxyInteractionType interactionType);

}
