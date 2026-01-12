package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void setLinkedRelPos(Long value) {
        BlockPos oldPos = this.getLinkedAbsPos();
        super.setLinkedRelPos(value);
        BlockPos newPos = this.getLinkedAbsPos();
        if (level != null && !level.isClientSide()) {
            level.neighborChanged(oldPos, this.getBlock(), oldPos);
            level.updateNeighborsAt(oldPos, this.getBlock());

            level.neighborChanged(newPos, this.getBlock(), newPos);
            level.updateNeighborsAt(newPos, this.getBlock());
        }
    }
}
