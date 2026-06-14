package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface FaceTargetingProxyBlockEntity<B extends FaceTargetingProxyBlock> {

    B getBlock();

    byte getFaceRotationWatermark();
    void setFaceRotationWatermark(byte watermark);

    default @NotNull TargetFace getTargetFace(BlockState state) {
        return getBlock().getTargetFace(state);
    }

    default boolean hasTargetFace(BlockState state) {
        return getBlock().hasTargetFace(state);
    }

    default void setTargetFace(Level level, BlockPos pos, BlockState state, TargetFace targetFace) {
        getBlock().setTargetFace(level, pos, state, targetFace);
    }

    default void setTargetFace(BlockState state, TargetFace targetFace) {
        getBlock().setTargetFace(state, targetFace);
    }

    default void setTargetFaceQuietly(Level level, BlockPos pos, BlockState state, TargetFace targetFace) {
        getBlock().setTargetFaceQuietly(level, pos, state, targetFace);
    }
}
