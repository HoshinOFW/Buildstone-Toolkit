package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PistonProxyBlockEntity extends IdProxyBlockEntity<PistonProxyBlockEntity> {
    public PistonProxyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BuildstoneBlockEntities.PISTON_PROXY.get(), blockPos, blockState);
    }

    @Override
    public Class<PistonProxyBlockEntity> selfClass() {
        return PistonProxyBlockEntity.class;
    }
}
