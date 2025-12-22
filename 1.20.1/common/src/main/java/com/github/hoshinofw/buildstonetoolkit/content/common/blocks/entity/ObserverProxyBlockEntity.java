package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ObserverProxyBlockEntity extends UpdateListenerProxyBlockEntity {
    public ObserverProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.OBSERVER_PROXY.get(), pos, state);
    }

}
