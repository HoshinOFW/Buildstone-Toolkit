package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IdProxyBlockEntity<T extends IdProxyBlockEntity<T>> extends ProxyBlockEntity implements IdObject {
    private static final IdRegistry<IdProxyBlockEntity<?>> idRegistry = new IdRegistry<>();

    public static IdRegistry<IdProxyBlockEntity<?>> getIdRegistry() {
        return idRegistry;
    }

    private long id = -1;

    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.registerNewToIdRegistry();
    }

    public void registerNewToIdRegistry() {
        this.setId(IdProxyBlockEntity.getIdRegistry().register(this));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        getIdRegistry().remove(this);
    }

    @Override
    public long getId() {
        return this.id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        Util.saveId(nbt, this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
    }

}
