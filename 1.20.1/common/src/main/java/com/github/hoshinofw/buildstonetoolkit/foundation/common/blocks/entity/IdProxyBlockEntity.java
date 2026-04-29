package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IdProxyBlockEntity<T extends IdProxyBlockEntity<T>> extends ProxyBlockEntity<T> implements IdObject {

    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static IdRegistry<IdProxyBlockEntity<?>> getIdRegistry(Level level) {
        return ProxyIdStorage.getIdRegistry(level);
    }

    protected long id = -1;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            if (id < 0) {
                this.id = ProxyIdStorage.getServerIdRegistry(serverLevel).registerNew(this);
                this.notifyUpdate();
            } else {
                if (ProxyIdStorage.getServerIdRegistry(serverLevel).hasEntry(this)) {
                    this.id = ProxyIdStorage.getServerIdRegistry(serverLevel).registerNew(this);
                    this.notifyUpdate();
                } else {
                    ProxyIdStorage.getServerIdRegistry(serverLevel).ensureEntry(this);
                }
            }
        }
    }

    @Override
    public void setRemoved() {
        //BuildstoneToolkit.LOGGER.info("setRemoved called");
        super.setRemoved();
        getIdRegistry(this.getLevel()).remove((IdProxyBlockEntity<?>) this);
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long id) {this.id = id;}

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        NBTUtil.saveId(nbt, this);
        //BuildstoneToolkit.LOGGER.info("saveAdditional called for nbt: {}", nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        loadLogic(this, nbt);
        super.load(nbt);
        //BuildstoneToolkit.LOGGER.info("loadAdditional called and ensureEntry called on idRegistry: {} for id: {}", getIdRegistry(this.getLevel()).getName(), this.getId());
    }

    public static void loadLogic(IdProxyBlockEntity<?> be, CompoundTag nbt) {
        be.setId(NBTUtil.getId(nbt));
        Level level = be.getLevel();
        if (level != null) {
            IdProxyBlockEntity.getIdRegistry(level).ensureEntry(be);
        }
    }
}
