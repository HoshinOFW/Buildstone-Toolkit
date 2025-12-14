package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PistonProxyBlockEntity extends ProxyBlockEntity {
    private final BlockPos.MutableBlockPos relativeTargetPos = BlockPos.ZERO.mutable();

    public PistonProxyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BuildstoneBlockEntities.PISTON_PROXY.get(), blockPos, blockState);
    }

    @Override
    public BlockPos getLinkedAbsPos() {
        return this.relativeTargetPos.offset(this.worldPosition);
    }

    @Override
    public Long getLinkedAbsLongPos() {
        return this.getLinkedAbsPos().asLong();
    }

    @Override
    public BlockPos getLinkedRelPos() {
        return this.relativeTargetPos;
    }

    @Override
    public Long getLinkedRelLongPos() {
        return this.getLinkedRelPos().asLong();
    }

    @Override
    public void setLinkedAbsPos(BlockPos value) {
        super.setLinkedAbsPos(value);
        this.setLinkedRelPos(value.subtract(this.worldPosition));
    }

    @Override
    public void setLinkedAbsPos(Long value) {
        super.setLinkedAbsPos(value);
        this.setLinkedAbsPos(BlockPos.of(value));
    }

    @Override
    public void setLinkedRelPos(BlockPos value) {
        super.setLinkedRelPos(value);
        this.relativeTargetPos.set(value);
    }

    @Override
    public void setLinkedRelPos(Long value) {
        super.setLinkedRelPos(value);
        this.relativeTargetPos.set(value);
    }
}
