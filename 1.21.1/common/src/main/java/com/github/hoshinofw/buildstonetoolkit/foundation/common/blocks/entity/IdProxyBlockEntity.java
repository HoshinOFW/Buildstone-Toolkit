package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

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

    private long id = -1;

    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static IdRegistry<IdProxyBlockEntity<?>> getIdRegistry(Level level) {
        return ProxyIdStorage.getIdRegistry(level);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        //TODO REPLACE WITH HOOK in IdProxy
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            if (id < 0) {
                this.id = ProxyIdStorage.getServerIdRegistry(serverLevel).registerNew(this);
                this.notifyUpdate();
                //BuildstoneToolkit.LOGGER.info("Registered NEW Id on the serverIdRegistry: {}, id: {}", ProxyIdStorage.getServerIdRegistry(serverLevel).getName(), this.id);
            } else {
                ProxyIdStorage.getServerIdRegistry(serverLevel).ensureEntry(this);
                //BuildstoneToolkit.LOGGER.info("Registered EXISTING Id on the serverIdRegistry: {}, id: {}", ProxyIdStorage.getServerIdRegistry(serverLevel).getName(), this.id);
            }
        }
    }

    @Override
    public void setRemoved() {
        //BuildstoneToolkit.LOGGER.info("setRemoved called");
        super.setRemoved();
        //TODO REPLACE WITH HOOK in IdProxy
        getIdRegistry(this.getLevel()).remove(this);
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long id) {this.id = id;}

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        //TODO REPLACE WITH HOOK in IdProxy
        NBTUtil.saveId(nbt, this);
        //BuildstoneToolkit.LOGGER.info("saveAdditional called for nbt: {}", nbt);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        //TODO REPLACE WITH HOOK in IdProxy
        this.id = NBTUtil.getId(nbt);
        if (level != null) {
            getIdRegistry(this.getLevel()).ensureEntry(this);
        }
        super.loadAdditional(nbt, registries);
        //BuildstoneToolkit.LOGGER.info("loadAdditional called and ensureEntry called on idRegistry: {} for id: {}", getIdRegistry(this.getLevel()).getName(), this.getId());
    }
}
