package com.github.hoshinofw.buildstonetoolkit.common.level.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PistonProxyBlockEntity extends SyncedBlockEntity {

    private BlockPos relativeTargetPos = new BlockPos.MutableBlockPos(0, 0, 0);

    public PistonProxyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BuildstoneBlockEntities.PISTON_PROXY.get(), blockPos, blockState);
    }

    public BlockPos getRelativeTargetPos() {
        return relativeTargetPos;
    }

    public void setRelativeTargetPos(BlockPos value) {
        this.relativeTargetPos = value;
    }


    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putIntArray("relativeTargetPos", Util.blockPosToArray(relativeTargetPos));
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.relativeTargetPos = Util.getTargetPosFromNBT(nbt);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        if (this.relativeTargetPos != null) {tag.putIntArray("relativeTargetPos", Util.blockPosToArray(this.relativeTargetPos));}
        return tag;
    }
}
