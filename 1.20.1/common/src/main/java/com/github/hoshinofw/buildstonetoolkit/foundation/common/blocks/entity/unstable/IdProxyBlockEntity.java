package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntityStable;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IdProxyBlockEntity<T extends IdProxyBlockEntity<T>> extends IdProxyBlockEntityStable<T> {
    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        NBTUtil.saveId(nbt, this);
        //BuildstoneToolkit.LOGGER.info("saveAdditional called for nbt: {}", nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        NBTUtil.IdBlockEntityLoadLogic(this, nbt);
        super.load(nbt);
        //BuildstoneToolkit.LOGGER.info("loadAdditional called and ensureEntry called on idRegistry: {} for id: {}", getIdRegistry(this.getLevel()).getName(), this.getId());
    }
}
