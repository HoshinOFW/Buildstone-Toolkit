package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.ProxyInteractionType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface InteractiveProxyBlock {

     void handleInteraction(ServerLevel level, BlockPos proxyPos, BlockState proxyState,
                                  Vec3 playerPosition, ProxyInteractionType[] interactionTypes);

     static int calculateSignal(double distance) {
          if (distance <= 2.0) return 1;
          if (distance >= 64.0) return 15;

          double t = (distance - 2.0) / (64.0 - 2.0);

          double value = 1.0 + 14.0 * (t * t);

          int signal = (int) Math.round(value);

          if (signal < 1) return 1;
          if (signal > 15) return 15;
          return signal;
     }

}
