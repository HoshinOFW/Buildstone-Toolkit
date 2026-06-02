package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class InteractiveProxyBlockEntity<B extends ProxyBlock<B, BE>, BE extends ProxyBlockEntity<B, BE>> extends ProxyBlockEntity<B, BE> {

    public InteractiveProxyBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
