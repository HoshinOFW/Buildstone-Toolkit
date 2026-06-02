package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface FaceTargetingProxyBlock {

    @NotNull TargetFace getTargetFace(BlockState state);

    default boolean hasTargetFace(BlockState state) {
        return getTargetFace(state) != TargetFace.ALL;
    }

    void setTargetFace(Level level, BlockPos pos, BlockState state, TargetFace targetFace);

    BlockState setTargetFace(BlockState state, TargetFace targetFace);

}
