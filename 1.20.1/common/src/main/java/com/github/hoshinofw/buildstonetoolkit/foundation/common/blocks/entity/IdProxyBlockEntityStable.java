package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.storage.unstable.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IdProxyBlockEntityStable<T extends IdProxyBlockEntity<T>> extends ProxyBlockEntity<T> implements IdObject {
    public IdProxyBlockEntityStable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        getIdRegistry(this.getLevel()).remove((IdProxyBlockEntity<?>) this);
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setId(long id) {this.id = id;}
}
