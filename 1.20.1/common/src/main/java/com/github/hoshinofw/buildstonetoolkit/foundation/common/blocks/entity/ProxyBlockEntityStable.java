package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

//Unstable and therefore missing methods: load and saveAdditional
public abstract class ProxyBlockEntityStable <T extends ProxyBlockEntity<T>> extends SyncedBlockEntity {

    protected final BlockPos.MutableBlockPos relativeTargetPos = BlockPos.ZERO.mutable();

    public ProxyBlockEntityStable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract Class<T> selfClass();

    public BlockPos getLinkedAbsPos() {
        return this.worldPosition.offset(this.relativeTargetPos);
    }
    public Long getLinkedAbsLongPos() {
        return this.worldPosition.offset(this.relativeTargetPos).asLong();
    }

    public BlockPos getLinkedRelPos() {
        return this.relativeTargetPos;
    }
    public Long getLinkedRelLongPos() {
        return this.relativeTargetPos.asLong();
    }

    public void setLinkedAbsPos(BlockPos value) {
        setLinkedRelPos(value.subtract(this.getBlockPos()));
    }
    public void setLinkedAbsPos(Long value){
        setLinkedAbsPos(BlockPos.of(value));
    }

    public void setLinkedRelPos(BlockPos value){
        this.setLinkedRelPos(value.asLong());
    }

    public void setLinkedRelPos(Long value){
        if (value != relativeTargetPos.asLong()) {
            this.relativeTargetPos.set(value);
            Level level = this.getLevel();
            if (level == null || level.isClientSide() || isVirtualRenderWorld(level)) {return;}
            this.notifyUpdate();
            this.getLevel().neighborChanged(this.getBlockPos(), this.getBlockState().getBlock(), this.getBlockPos());
        }
    }

    private static boolean isVirtualRenderWorld(Level level) {
        if (level == null) return false;
        return level.getClass().getName().equals("com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld");
    }
}
