package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RegisteredProxyBlockEntity<T extends RegisteredProxyBlockEntity<T>> extends IdProxyBlockEntity<T>{
    public RegisteredProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        getRegistry().removeProxy(self());
    }

    public abstract ProxyRegistry<T> getRegistry();

    @Override
    public BlockPos getLinkedAbsPos() {
        return getRegistry().getBlockPosTargetOf(self());
    }

    @Override
    public Long getLinkedAbsLongPos() {
        return getRegistry().getLongTargetOf(self());
    }

    @Override
    public BlockPos getLinkedRelPos() {
        return this.getLinkedAbsPos().subtract(this.worldPosition);
    }

    @Override
    public Long getLinkedRelLongPos() {
        return this.getLinkedRelPos().asLong();
    }

    @Override
    public void setLinkedAbsPos(BlockPos value) {
        super.setLinkedAbsPos(value);
        getRegistry().replaceLink(self(), value);
    }

    @Override
    public void setLinkedAbsPos(Long value) {
        super.setLinkedAbsPos(value);
        getRegistry().replaceLink(self(), value);
    }

    @Override
    public void setLinkedRelPos(BlockPos value) {
        super.setLinkedRelPos(value);
        getRegistry().replaceLink(self(), this.worldPosition.offset(value));
    }

    @Override
    public void setLinkedRelPos(Long value) {
        super.setLinkedRelPos(value);
        this.setLinkedRelPos(BlockPos.of(value));
    }
}
