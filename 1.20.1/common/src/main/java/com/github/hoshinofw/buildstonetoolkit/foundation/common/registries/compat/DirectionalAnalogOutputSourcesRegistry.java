package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.compat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DirectionalAnalogOutputSourcesRegistry {

    private static final ObjectArrayList<DirectionalAnalogOutputSource> sources = new ObjectArrayList<>();

    public static void register(DirectionalAnalogOutputSource source) {
        sources.add(source);
    }

    public static int get(BlockState blockState, Level level,  BlockPos blockPos, Direction dir) {
        int output = 0;
        for (DirectionalAnalogOutputSource source : sources) {
            int signal = source.getAnalogOutputSignalFrom(blockState, level, blockPos, dir);
            if (signal >= 15) return 15;
            if (signal > output) output = signal;
        }
        return output;
    }

    public static boolean isEmpty() {
        return sources.isEmpty();
    }

    public interface DirectionalAnalogOutputSource {
        int getAnalogOutputSignalFrom(final BlockState blockState, final Level level, final BlockPos blockPos, final Direction dir);
    }

}
