package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpdateListenerProxyBlockEntity<B extends UpdateListenerProxyBlock<B, BE>, BE extends UpdateListenerProxyBlockEntity<B, BE>> extends ProxyBlockEntity<B, BE>{
    public UpdateListenerProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

}
