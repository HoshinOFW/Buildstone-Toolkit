package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneProxyBlockEntity extends UpdateListenerProxyBlockEntity {

    public RedstoneProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.REDSTONE_PROXY.get(), pos, state);
    }

    public int getSignal() {
        return RedstoneProxyBlock.getSignal(this.getBlockState());
    }

    public RedstoneProxyBlock getBlock() {
        return (RedstoneProxyBlock) (this.getBlockState().getBlock());
    }
}
