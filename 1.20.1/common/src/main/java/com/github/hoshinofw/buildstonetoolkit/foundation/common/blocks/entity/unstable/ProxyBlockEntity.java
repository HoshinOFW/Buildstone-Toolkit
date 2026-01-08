package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntityStable;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ProxyBlockEntity<T extends ProxyBlockEntity<T>> extends ProxyBlockEntityStable<T> {
    public ProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        NBTUtil.saveTargetNBTFromProxy(nbt, this);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.setLinkedAbsPos(NBTUtil.getTargetPosFromNBT(nbt));
    }
}
