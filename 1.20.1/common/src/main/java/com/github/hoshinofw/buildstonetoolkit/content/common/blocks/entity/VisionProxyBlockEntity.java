package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.VisionProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.InteractiveProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VisionProxyBlockEntity extends InteractiveProxyBlockEntity<VisionProxyBlock, VisionProxyBlockEntity> {
    public VisionProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.VISION_PROXY.get(), pos, state);
    }

    @Override
    public Class<VisionProxyBlockEntity> selfClass() {
        return VisionProxyBlockEntity.class;
    }

    @Override
    public VisionProxyBlock getBlock() {
        return VisionProxyBlock.getBlock();
    }
}
