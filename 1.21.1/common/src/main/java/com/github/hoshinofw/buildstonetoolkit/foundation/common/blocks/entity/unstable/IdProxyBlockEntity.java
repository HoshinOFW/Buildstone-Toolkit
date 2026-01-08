package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntityStable;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IdProxyBlockEntity<T extends IdProxyBlockEntity<T>> extends IdProxyBlockEntityStable<T> {

    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        NBTUtil.saveId(nbt, this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        NBTUtil.IdBlockEntityLoadLogic(this, nbt);
        super.loadAdditional(nbt, registries);
    }
}
