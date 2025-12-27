package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

//Unstable and therefore missing methods: load and saveAdditional
public abstract class ProxyBlockEntityStable <T extends ProxyBlockEntity<T>> extends SyncedBlockEntity {

    private final BlockPos.MutableBlockPos relativeTargetPos = BlockPos.ZERO.mutable();

    public ProxyBlockEntityStable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract Class<T> selfClass();

    public BlockPos getLinkedAbsPos() {
        return this.worldPosition.offset(this.relativeTargetPos);
    }
    public Long getLinkedAbsLongPos() {
        return this.worldPosition.offset(this.relativeTargetPos).asLong();
    }

    public BlockPos getLinkedRelPos() {
        return this.relativeTargetPos;
    }
    public Long getLinkedRelLongPos() {
        return this.relativeTargetPos.asLong();
    }

    public void setLinkedAbsPos(BlockPos value) {
        setLinkedRelPos(value.subtract(this.getBlockPos()));
    }
    public void setLinkedAbsPos(Long value){
        setLinkedAbsPos(BlockPos.of(value));
    }

    public void setLinkedRelPos(BlockPos value){
        if (value.asLong() != relativeTargetPos.asLong()) {
            this.relativeTargetPos.set(value);
            this.notifyUpdate();
            if (this.getLevel() == null) {return;}
            this.getLevel().neighborChanged(this.getBlockPos(), this.getBlockState().getBlock(), this.getBlockPos());
        }
    }
    public void setLinkedRelPos(Long value){
        if (value != relativeTargetPos.asLong()) {
            this.relativeTargetPos.set(value);
            this.notifyUpdate();
            if (this.getLevel() == null) {return;}
            this.getLevel().neighborChanged(this.getBlockPos(), this.getBlockState().getBlock(), this.getBlockPos());
        }
    }
}
