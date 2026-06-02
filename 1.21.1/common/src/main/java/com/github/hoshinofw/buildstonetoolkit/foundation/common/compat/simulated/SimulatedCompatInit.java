package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.simulated;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.compat.DirectionalAnalogOutputSourcesRegistry;
import dev.simulated_team.simulated.api.IDirectionalAnalogOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SimulatedCompatInit {

    public static void init() {
        DirectionalAnalogOutputSourcesRegistry.register(new DirectionalAnalogOutputSourcesRegistry.DirectionalAnalogOutputSource() {
            @Override
            public int getAnalogOutputSignalFrom(BlockState blockState, Level level, BlockPos blockPos, Direction dir) {
                if (blockState.getBlock() instanceof IDirectionalAnalogOutput analogOutputBlock) {
                    return analogOutputBlock.getAnalogOutputSignalFrom(blockState, level, blockPos, dir);
                }
                return 0;
            }
        });

        BuildstoneToolkit.LOGGER.info("Simulated Compat done");
    }

}
