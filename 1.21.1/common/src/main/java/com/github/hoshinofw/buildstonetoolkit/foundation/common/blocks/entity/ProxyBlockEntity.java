package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class ProxyBlockEntity<B extends ProxyBlock<B, BE>, BE extends ProxyBlockEntity<B, BE>> extends SyncedBlockEntity implements IdObject {

    @ShadowVersion
    public ProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ShadowVersion
    public void setTargetPos(@NotNull BlockPos value);

    @ShadowVersion
    public void relocateTargetPos(@NotNull BlockPos newAbsPos);

    @ShadowVersion
    public BlockPos getTargetPos();

    @ShadowVersion
    @ModifySignature("saveAdditional")
    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries);

    @ShadowVersion
    @ModifySignature("load")
    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries);

    @ShadowVersion
    public void adoptIdFrom(ProxyBlockEntity<?, ?> other);

    @ShadowVersion
    public static IdRegistry<ProxyBlockEntity<?, ?>> getIdRegistry(Level level);

    @NotNull
    @ShadowVersion
    public static ProxyRegistry<ProxyBlockEntity<?, ?>> getRegistry(Level level);

    @ShadowVersion
    public void setTargetId(long targetId);


}
