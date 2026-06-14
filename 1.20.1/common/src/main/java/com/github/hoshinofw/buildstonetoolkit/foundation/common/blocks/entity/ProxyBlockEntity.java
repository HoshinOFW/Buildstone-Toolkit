package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders.ProxyRegistryHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ProxyBlockEntity<B extends ProxyBlock<B, BE>, BE extends ProxyBlockEntity<B, BE>> extends SyncedBlockEntity implements IdObject {

    public static final ThreadLocal<Boolean> storePositionAsRelative = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> storeTargetId = ThreadLocal.withInitial(() -> true);

    protected final BlockPos.MutableBlockPos relativeTargetPos = BlockPos.ZERO.mutable();
    protected long targetId = -1;
    protected long id = -1;

    public ProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    public static ProxyRegistry<ProxyBlockEntity<?, ?>> getRegistry(Level level) {
        return ((ProxyRegistryHolder) level).buildstonetoolkit$getProxyRegistry();
    }

    @NotNull
    public ProxyRegistry<ProxyBlockEntity<?, ?>> getRegistry() {
        return getRegistry(Objects.requireNonNull(this.getLevel()));
    }

    public static IdRegistry<ProxyBlockEntity<?, ?>> getIdRegistry(Level level) {
        return getRegistry(level).idRegistry;
    }

    public static @NotNull TargetIdRegistry getTargetIdRegistry(ServerLevel level) {
        return getRegistry(level).targetIdRegistry;
    }

    public boolean isActive() {
        return getBlock().isActive(getBlockState());
    }

    static boolean isValidId(long id) {return id >= 0;}

    public abstract Class<BE> selfClass();

    public abstract B getBlock();

    public BlockPos getTargetPos() {
        return this.worldPosition.offset(this.relativeTargetPos);
    }

    public long getTargetLongPos() {
        return Util.fastOffset(this.worldPosition.asLong(), this.relativeTargetPos.asLong());
    }

    public BlockPos getRelTargetPos() {
        return this.relativeTargetPos;
    }

    public long getRelTargetLongPos() {
        return this.relativeTargetPos.asLong();
    }

    public void setTargetPos(@NotNull BlockPos value) {
        setRelTargetPos(value.subtract(this.getBlockPos()));
    }

    public void setTargetPos(long value){
        setTargetPos(BlockPos.of(value));
    }

    public void setRelTargetPos(@NotNull BlockPos value){
        this.setRelTargetPos(value.asLong());
    }

    public void setRelTargetPos(long value){
        long newAbsPos = Util.fastOffset(this.worldPosition.asLong(), value);

        //TODO In theory activation/deactivation doesnt touch registries or refcounts so this works.

        if (value == TargetIdRegistry.NULL_POS) {
            ProxyTargetResolver.setActiveGuarded(this, false);
            return;
        }

        ProxyTargetResolver.relinkRegistry(this, newAbsPos);

        if (value != this.relativeTargetPos.asLong()) {
            ProxyTargetResolver.rebind(this, newAbsPos);
            this.relativeTargetPos.set(value);
            ProxyTargetResolver.fireUpdates(this, true);
        }

        ProxyTargetResolver.setActiveGuarded(this, true);
    }


    void setRelTargetSilent(long value) {
        long newAbsPos = Util.fastOffset(this.worldPosition.asLong(), value);

        ProxyTargetResolver.relinkRegistry(this, newAbsPos);

        if (value != this.relativeTargetPos.asLong()) {
            long oldAbs = getTargetLongPos();
            this.relativeTargetPos.set(value);
            ProxyTargetResolver.fireUpdates(this, false);
            ProxyTargetResolver.syncActiveOnTargetChange(this, oldAbs, newAbsPos);
        }
    }

    void setTargetSilent(long value) {
        setRelTargetSilent(BlockPos.of(value).subtract(this.getBlockPos()).asLong());
    }

    public void relocateTargetPos(long newAbsPos) {
        ProxyTargetResolver.relocate(this, newAbsPos);
    }

    public void relocateTargetPos(@NotNull BlockPos newAbsPos) {
        relocateTargetPos(newAbsPos.asLong());
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        ProxyTargetResolver.saveTo(this, nbt);
        ProxyIdResolver.saveTo(this, nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        ProxyIdResolver.loadFrom(this, nbt);
        ProxyTargetResolver.loadFrom(this, nbt);
    }


    @Override
    public void setLevel(@NotNull Level level) {
        super.setLevel(level);
        if (level instanceof ServerLevel serverLevel) {
            ProxyIdResolver.reconcileOnSetLevel(this, serverLevel);
            ProxyTargetResolver.reconcileOnSetLevel(this, serverLevel);
        } else if (level.isClientSide()) {
            ProxyIdResolver.reconcileClientOnSetLevel(this);
        }
        ProxyTargetResolver.relinkRegistry(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (isGenuineRemoval()) {
            ProxyIdResolver.onRemoved(this);
            ProxyTargetResolver.onRemoved(this);
        }
    }

    private boolean isGenuineRemoval() {
        Level level = this.getLevel();
        if (level == null) return false;
        BlockPos pos = this.getBlockPos();

        LevelChunk chunk = level.getChunkSource().getChunk(
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()),
                false);
        if (chunk == null) return false;
        return chunk.getBlockEntity(pos) != this;
    }

    public void adoptIdFrom(ProxyBlockEntity<?, ?> other) {
        if (!ProxyBlockEntity.isValidId(other.id)) return;
        if (!(this.getLevel() instanceof ServerLevel)) return;

        ProxyIdResolver.adopt(this, other);
        ProxyTargetResolver.adopt(this, other);

        other.id = -1;
        other.targetId = -1;

        this.notifyUpdate();
    }

    @Override
    public long getId() {
        return this.id;
    }

    public long getTargetId() {
        return this.targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }
}
