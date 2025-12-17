package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.storage.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IdProxyBlockEntity<T extends IdProxyBlockEntity<T>> extends ProxyBlockEntity<T> implements IdObject {
    public static IdRegistry<IdProxyBlockEntity<?>> getIdRegistry(Level level) {
        return ProxyIdStorage.getIdRegistry(level);
    }

    private long id = -1;

    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            if (id < 0) {
                this.id = ProxyIdStorage.getServerIdRegistry(serverLevel).registerNew(this);
                this.notifyUpdate();
                BuildstoneToolkit.LOGGER.info("Registered NEW Id on the serverIdRegistry: {}, id: {}", ProxyIdStorage.getServerIdRegistry(serverLevel).getName(), this.id);
            } else {
                ProxyIdStorage.getServerIdRegistry(serverLevel).ensureEntry(this);
                BuildstoneToolkit.LOGGER.info("Registered EXISTING Id on the serverIdRegistry: {}, id: {}", ProxyIdStorage.getServerIdRegistry(serverLevel).getName(), this.id);
            }
        }
    }

    @Override
    public void setRemoved() {
        //BuildstoneToolkit.LOGGER.info("setRemoved called");
        super.setRemoved();
        getIdRegistry(this.getLevel()).remove(this);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        NBTUtil.saveId(nbt, this);
        BuildstoneToolkit.LOGGER.info("saveAdditional called for nbt: {}", nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.id = NBTUtil.getId(nbt);
        if (level != null) {
            getIdRegistry(this.getLevel()).ensureEntry(this);
        }
        super.load(nbt);
        BuildstoneToolkit.LOGGER.info("loadAdditional called and ensureEntry called on idRegistry: {} for id: {}", getIdRegistry(this.getLevel()).getName(), this.getId());
    }
}
