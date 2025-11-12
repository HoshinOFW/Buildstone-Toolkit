package com.github.hoshinofw.buildstonetoolkit.common.level.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GuidedProxyBlockEntity extends SyncedBlockEntity {

    private int link_distance = 0;

    public GuidedProxyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BuildstoneBlockEntities.GUIDED_PROXY.get(), blockPos, blockState);
    }

    public int getDistance() {
        return this.link_distance;
    }

    public void setDistance(int newDistance) {
        this.link_distance = newDistance;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("link_distance", this.link_distance);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.link_distance = nbt.getInt("link_distance");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag nbt = super.getUpdateTag(provider);
        nbt.putInt("link_distance", this.link_distance);
        return nbt;
    }
}
