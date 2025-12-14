package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class ProxyBlockEntity extends SyncedBlockEntity{
    public ProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        Util.saveTargetNBTFromProxy(nbt, this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.setLinkedAbsPos(Util.getTargetPosFromNBT(nbt));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.saveAdditional(tag, provider);
        return tag;
    }

    public abstract BlockPos getLinkedAbsPos();
    public abstract Long getLinkedAbsLongPos();

    public abstract BlockPos getLinkedRelPos();
    public abstract Long getLinkedRelLongPos();

    public void setLinkedAbsPos(BlockPos value) {
        this.setChanged();
    }
    public void setLinkedAbsPos(Long value){
        this.setChanged();
    }

    public void setLinkedRelPos(BlockPos value){
        this.setChanged();
    }
    public void setLinkedRelPos(Long value){
        this.setChanged();
    }
}
