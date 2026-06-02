package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class PistonProxyBlockEntity extends ProxyBlockEntity<PistonProxyBlock, PistonProxyBlockEntity> {
    public PistonProxyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BuildstoneBlockEntities.PISTON_PROXY.get(), blockPos, blockState);
    }

    @Override
    public Class<PistonProxyBlockEntity> selfClass() {
        return PistonProxyBlockEntity.class;
    }

    @Override
    public PistonProxyBlock getBlock() {
        return PistonProxyBlock.getBlock();
    }
}
