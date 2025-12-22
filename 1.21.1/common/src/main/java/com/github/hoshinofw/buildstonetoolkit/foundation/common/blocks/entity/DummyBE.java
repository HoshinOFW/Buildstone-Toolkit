package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DummyBE extends BlockEntity {
    public DummyBE(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.GUIDED_PROXY.get(), pos, state);
    }
}