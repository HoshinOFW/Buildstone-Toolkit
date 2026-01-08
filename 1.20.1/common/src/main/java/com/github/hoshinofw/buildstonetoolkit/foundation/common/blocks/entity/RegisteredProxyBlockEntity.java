package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class RegisteredProxyBlockEntity<T extends RegisteredProxyBlockEntity<T>> extends IdProxyBlockEntity<T> {
    public RegisteredProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @NotNull
    public abstract ProxyRegistry<T> getRegistry();


    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level == null) return;
        if (getLinkedRelLongPos() != 0L) {
            getRegistry().replaceLink(self(), getLinkedAbsPos());
        }
    }

    @Override
    public void setRemoved() {
        getRegistry().removeProxy(self());
        super.setRemoved();
    }

    @Override
    public void setLinkedAbsPos(BlockPos value) {
        super.setLinkedAbsPos(value);
        if (this.getLevel() == null) return;
        getRegistry().replaceLink(self(), value);
    }

    @Override
    public void setLinkedAbsPos(Long value) {
        super.setLinkedAbsPos(value);
        if (this.getLevel() == null) return;
        getRegistry().replaceLink(self(), value);
    }

    @Override
    public void setLinkedRelPos(BlockPos value) {
        super.setLinkedRelPos(value);
        if (this.getLevel() == null) return;
        getRegistry().replaceLink(self(), this.worldPosition.offset(value));
    }

    @Override
    public void setLinkedRelPos(Long value) {
        super.setLinkedRelPos(value);
        if (this.getLevel() == null) return;
        getRegistry().replaceLink(self(), this.worldPosition.offset(BlockPos.of(value)));
    }
}
