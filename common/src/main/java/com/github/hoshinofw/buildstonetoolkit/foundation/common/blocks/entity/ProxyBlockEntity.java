package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ProxyBlockEntity<T extends ProxyBlockEntity<T>> extends SyncedBlockEntity{

    private final BlockPos.MutableBlockPos relativeTargetPos = BlockPos.ZERO.mutable();

    public ProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract Class<T> selfClass();

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        NBTUtil.saveTargetNBTFromProxy(nbt, this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        this.setLinkedAbsPos(NBTUtil.getTargetPosFromNBT(nbt));
    }

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
        }
    }
    public void setLinkedRelPos(Long value){
        if (value != relativeTargetPos.asLong()) {
            this.relativeTargetPos.set(value);
            this.notifyUpdate();
        }
    }
}
