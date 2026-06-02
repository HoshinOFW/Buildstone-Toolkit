package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.InteractionProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.InteractiveProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InteractionProxyBlockEntity extends InteractiveProxyBlockEntity<InteractionProxyBlock, InteractionProxyBlockEntity> {
    public InteractionProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.INTERACTION_PROXY.get(), pos, state);
    }

    @Override
    public Class<InteractionProxyBlockEntity> selfClass() {
        return InteractionProxyBlockEntity.class;
    }

    @Override
    public InteractionProxyBlock getBlock() {
        return InteractionProxyBlock.getBlock();
    }
}
