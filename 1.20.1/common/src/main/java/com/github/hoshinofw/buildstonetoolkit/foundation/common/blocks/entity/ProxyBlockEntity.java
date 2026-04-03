package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class ProxyBlockEntity<T extends ProxyBlockEntity<T>> extends SyncedBlockEntity {
    protected final BlockPos.MutableBlockPos relativeTargetPos = BlockPos.ZERO.mutable();

    public ProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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

    public void setLinkedAbsPos(@NotNull BlockPos value) {
        setLinkedRelPos(value.subtract(this.getBlockPos()));
    }
    public void setLinkedAbsPos(Long value){
        setLinkedAbsPos(BlockPos.of(value));
    }

    public void setLinkedRelPos(@NotNull BlockPos value){
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
