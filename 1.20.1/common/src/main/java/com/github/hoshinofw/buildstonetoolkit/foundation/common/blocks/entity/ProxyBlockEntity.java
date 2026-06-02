package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
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

    private static boolean isValidId(long id) {return id >= 0;}

    public abstract Class<BE> selfClass();

    public BlockPos getLinkedAbsPos() {
        return this.worldPosition.offset(this.relativeTargetPos);
    }

    public long getLinkedAbsLongPos() {
        return this.worldPosition.offset(this.relativeTargetPos).asLong();
    }

    public BlockPos getLinkedRelPos() {
        return this.relativeTargetPos;
    }
    public long getLinkedRelLongPos() {
        return this.relativeTargetPos.asLong();
    }

    //TODO remove nested registry calls or improve how the methods are wired
    public void setLinkedAbsPos(@NotNull BlockPos value) {
        setLinkedRelPos(value.subtract(this.getBlockPos()));
    }

    public void setLinkedAbsPos(long value){
        setLinkedAbsPos(BlockPos.of(value));
    }

    public void setLinkedRelPos(@NotNull BlockPos value){
        this.setLinkedRelPos(value.asLong());
    }

    public void setLinkedRelPos(long value){
        Level level = this.getLevel();
        long newAbsPos = this.worldPosition.offset(BlockPos.of(value)).asLong();

        syncTargetLink(value, newAbsPos);

        if (value != relativeTargetPos.asLong()) {
            rebindTargetId(value, newAbsPos);
            this.relativeTargetPos.set(value);
            if (level == null || level.isClientSide() || Util.isVirtualRenderWorld(level)) {return;}
            this.notifyUpdate();
            level.neighborChanged(this.getBlockPos(), this.getBlockState().getBlock(), this.getBlockPos());
        }
    }

    /** Doesn't try to send an update to the client
     * @param value relative BlockPos packed as a long.*/
    private void silentSetLinkedRelPos(long value) {
        long newAbsPos = this.worldPosition.offset(BlockPos.of(value)).asLong();
        syncTargetLink(value, newAbsPos);

        if (value != relativeTargetPos.asLong()) {
            this.relativeTargetPos.set(value);
            Level level = this.getLevel();
            if (level == null || level.isClientSide() || Util.isVirtualRenderWorld(level)) {return;}
            this.getLevel().neighborChanged(this.getBlockPos(), this.getBlockState().getBlock(), this.getBlockPos());
        }
    }
    /** Doesn't try to send an update to the client
     * @param value absolute BlockPos packed as a long.*/
    private void silentSetLinkedAbsPos(long value) {
        silentSetLinkedRelPos(BlockPos.of(value).subtract(this.getBlockPos()).asLong());
    }

    public void relocateTargetPos(long newAbsPos) {
        long newRel = BlockPos.of(newAbsPos).subtract(this.getBlockPos()).asLong();
        syncTargetLink(newRel, newAbsPos);

        if (newRel != relativeTargetPos.asLong()) {
            this.relativeTargetPos.set(newRel);
            Level level = this.getLevel();
            if (level == null || level.isClientSide() || Util.isVirtualRenderWorld(level)) return;
            this.notifyUpdate();
            level.neighborChanged(this.getBlockPos(), this.getBlockState().getBlock(), this.getBlockPos());
        }
    }

    public void relocateTargetPos(@NotNull BlockPos newAbsPos) {
        relocateTargetPos(newAbsPos.asLong());
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        saveAdditionalStable(nbt);
    }

    public void saveAdditionalStable(CompoundTag nbt) {
        if (storePositionAsRelative.get()) {
            NBTUtil.saveRelativeTargetNBTFromProxy(nbt, this);
        } else {
            NBTUtil.saveAbsoluteTargetNBTFromProxy(nbt, this);
        }
        if (storeTargetId.get() && TargetIdRegistry.isValidTargetId(this.targetId)) {
            NBTUtil.saveTargetId(nbt, this.targetId);
        }
        NBTUtil.saveId(nbt, this);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        stableLoadLogic(nbt);
    }

    public void stableLoadLogic(CompoundTag nbt) {
        idLoadLogic(nbt);

        boolean hasTargetIdNbt = nbt.contains(NBTUtil.NBTTargetIdKey, CompoundTag.TAG_LONG);

        if (nbt.contains(NBTUtil.NBTAbsTargetPosKey, CompoundTag.TAG_LONG))  {
            long absPos = NBTUtil.getAbsoluteTargetPosFromNBT(nbt);
            this.silentSetLinkedAbsPos(absPos);
            if (!hasTargetIdNbt) claimTargetId();
        } else if (nbt.contains(NBTUtil.NBTRelTargetPosKey, CompoundTag.TAG_LONG)) {
            long relPos = NBTUtil.getRelativeTargetNBTFromPRoxy(nbt);
            this.silentSetLinkedRelPos(relPos);
            if (!hasTargetIdNbt) claimTargetId();
        }

        if (hasTargetIdNbt) {
            long loadedTargetId = NBTUtil.getTargetId(nbt);
            this.setTargetId(loadedTargetId);
            if (TargetIdRegistry.isValidTargetId(loadedTargetId) && this.getLevel() instanceof ServerLevel serverLevel) {
                getTargetIdRegistry(serverLevel).lookup(loadedTargetId).ifPresentOrElse(
                        this::silentSetLinkedAbsPos,
                        () -> BuildstoneToolkit.LOGGER.warn("stableLoadLogic: targetId {} not found in TargetIdRegistry", loadedTargetId));
            }
        }
    }

    //TODO test what exactly notifyUpdate() seems to break.
    //TODO Clean up this method
    @Override
    public void setLevel(@NotNull Level level) {
        super.setLevel(level);
        //BuildstoneToolkit.LOGGER.info("setLevel called! level = {}", level.getClass().getName());
        if (!level.isClientSide()){
            if (level instanceof ServerLevel serverLevel) {
                ServerIdRegistry<ProxyBlockEntity<?, ?>> idRegistry = ProxyIdStorage.getServerIdRegistry(serverLevel);

                if (!ProxyBlockEntity.isValidId(getId())) {
                    //If invalid, register anew.
                    this.setId(idRegistry.registerNew(this));

                } else {
                    //If valid, check if a new id must be assigned.
                    if (idRegistry.hasEntry(this.getId())) {

                        ProxyBlockEntity<?, ?> existingBe = idRegistry.getEntry(this.getId());
                        if (existingBe == null) {
                            throw new RuntimeException("I hope this doesn't happen part 1");
                        } else if (existingBe == this || existingBe.isRemoved() || serverLevel.getBlockEntity(existingBe.getBlockPos()) != existingBe) {
                            idRegistry.ensureEntry(this);
                        } else {
                            long oldId = getId();
                            setId(idRegistry.registerNew(this));
                            getRegistry().removeProxy(oldId);
                            idRegistry.recycleAndEnsure(oldId, this);
                        }
                    } else {
                        idRegistry.ensureEntry(this);
                    }
                }

                if (TargetIdRegistry.isValidTargetId(this.targetId)) {
                    long currentAbs = this.getLinkedAbsLongPos();
                    getTargetIdRegistry(serverLevel).lookup(this.targetId).ifPresentOrElse(
                            this::silentSetLinkedAbsPos,
                            () -> recoverDanglingTargetId(currentAbs)
                    );
                } else {
                    claimTargetId();
                }
            }
        } else {
            IdRegistry<ProxyBlockEntity<?, ?>> clientRegistry =  ProxyIdStorage.getClientRegistry();
            if (ProxyBlockEntity.isValidId(getId())
            ) {
               clientRegistry.ensureEntry(this);
            }
        }

        getRegistry().replaceLink(this, getLinkedAbsPos());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        Level level = this.getLevel();
        if (level == null) return;
        BlockPos pos = this.getBlockPos();

        LevelChunk chunk = level.getChunkSource().getChunk(
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()),
                false);
        if (chunk == null) return;
        if (chunk.getBlockEntity(pos) != this) {
            //Proxy actually has been removed.
            removeFromRegistries();
        }
    }

    private void removeFromRegistries() {
        getRegistry().removeProxy(this);
        getIdRegistry(this.getLevel()).remove(this);
        releaseTargetId();
    }

    @Override
    public long getId() {
        return this.id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public long getTargetId() {
        return this.targetId;
    }

    private void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void adoptIdFrom(ProxyBlockEntity<?, ?> other) {
        long otherId = other.getId();
        if (!ProxyBlockEntity.isValidId(otherId)) return;

        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        ServerIdRegistry<ProxyBlockEntity<?, ?>> registry = ProxyIdStorage.getServerIdRegistry(serverLevel);
        long placeholderId = this.getId();

        this.setId(otherId);
        if (ProxyBlockEntity.isValidId(placeholderId) && placeholderId != otherId) {
            getRegistry().removeProxy(placeholderId);
            registry.recycleAndEnsure(placeholderId, this);
        } else {
            registry.ensureEntry(this);
        }

        inheritTargetIdFrom(other);

        syncTargetLink(getLinkedRelLongPos(), getLinkedAbsLongPos());

        // Reset other's id/targetId so its setRemoved (when Sable disposes the source)
        // does not clobber our newly-claimed proxy id or release the inherited targetId.
        other.setId(-1);
        other.targetId = -1;

        this.notifyUpdate();
    }

    //TODO This method is a mess
    public void idLoadLogic(CompoundTag nbt) {
        Level level = this.getLevel();
        long newId = NBTUtil.getId(nbt);

        if (level == null) {
            //BuildstoneToolkit.LOGGER.info("loadLogic reached! level is null!");
            //World is still initializing, set id.
            setId(newId);
            this.setChanged();
            return;
        }

        //BuildstoneToolkit.LOGGER.info("loadLogic reached! level = {}", level.getClass().getName());

        if (level.isClientSide()) {
            setId(newId);
            IdRegistry<ProxyBlockEntity<?, ?>> clientRegistry =  ProxyIdStorage.getClientRegistry();
            if (ProxyBlockEntity.isValidId(newId) && !clientRegistry.hasEntry(this)) {
                clientRegistry.ensureEntry(this);
            }

        } else if (level instanceof ServerLevel serverLevel) {
            //BuildstoneToolkit.LOGGER.info("loadLogic serverside reached");
            ServerIdRegistry<ProxyBlockEntity<?, ?>> registry = ProxyIdStorage.getServerIdRegistry(serverLevel);

            if (ProxyBlockEntity.isValidId(newId)) {
                if (!registry.hasEntry(newId)) {
                    // Use newId
                    long oldId = getId();
                    if (ProxyBlockEntity.isValidId(oldId)) {
                        //BuildstoneToolkit.LOGGER.info("loadLogic in IdPBE 1");
                        setId(newId);
                        getRegistry().removeProxy(oldId);
                        registry.recycleAndEnsure(oldId, this);
                    } else {
                        //BuildstoneToolkit.LOGGER.info("loadLogic in IdPBE 2");
                        setId(newId);
                        registry.ensureEntry(this);
                    }
                    this.notifyUpdate();
                } else {
                    ProxyBlockEntity<?, ?> existingBe = registry.getEntry(newId);
                    if (existingBe == null) {
                        throw new RuntimeException("I hope this doesn't happen part 2");
                    } else if (existingBe == this) {
                        registry.ensureEntry(this);
                    } else if (existingBe.isRemoved() || serverLevel.getBlockEntity(existingBe.getBlockPos()) != existingBe) {
                        //Take the new id if existingBe in the registry is removed or no longer in the world.
                        //BuildstoneToolkit.LOGGER.info("loadLogic in IdPBE 3");
                        setId(newId);
                        registry.ensureEntry(this);
                        this.notifyUpdate();
                    }
                }
            } else {
                //Keep oldId
                //BuildstoneToolkit.LOGGER.info("loadLogic in IdPBE 4");
                registry.ensureEntry(this);
            }
        }
    }

    public abstract B getBlock();

    /** Sync ProxyRegistry link to {@code newAbsPos}. */
    private void syncTargetLink(long newRel, long newAbsPos) {
        if (this.getLevel() == null) return;
        getRegistry().replaceLink(this, newAbsPos);
    }

    /** Release current targetId, then acquire for {@code newAbsPos}.
     *  Tuner-style retarget path; server-only. */
    private void rebindTargetId(long newRel, long newAbsPos) {
        if (!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        TargetIdRegistry tir = getTargetIdRegistry(serverLevel);
        tir.release(this.targetId);
        this.targetId = tir.acquire(newAbsPos);
    }

    /** Acquire a targetId for the current target iff this proxy is active .
     *  Joining path (no release); server-only. */
    private void claimTargetId() {
        if (!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        this.targetId = getTargetIdRegistry(serverLevel).acquire(this.getLinkedAbsLongPos());
    }

    /** Re-acquire for {@code currentAbs} if active, else clear. For setLevel lookup-miss. */
    private void recoverDanglingTargetId(long currentAbs) {
        if (!(this.getLevel() instanceof ServerLevel serverLevel)) return;

        BuildstoneToolkit.LOGGER.warn("setLevel: targetId {} dangling; re-acquiring for {}.", this.targetId, currentAbs);
        this.targetId = getTargetIdRegistry(serverLevel).acquire(currentAbs);

    }

    /** Release this proxy's targetId and clear the field. Server-only. */
    private void releaseTargetId() {
        if (!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        getTargetIdRegistry(serverLevel).release(this.targetId);
        this.targetId = -1L;
    }

    /** Take {@code other}'s targetId as our own; releases any placeholder first, no new acquire. */
    private void inheritTargetIdFrom(ProxyBlockEntity<?, ?> other) {
        if (this.targetId == other.targetId) return;
        if (!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        getTargetIdRegistry(serverLevel).release(this.targetId);
        this.targetId = other.targetId;
    }
}

