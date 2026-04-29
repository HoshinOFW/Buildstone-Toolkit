package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.core.BuildstoneToolkitClient;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdObject;
import com.github.hoshinofw.multiversion.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@ModifyClass
public abstract class IdProxyBlockEntity<T extends IdProxyBlockEntity<T>> extends ProxyBlockEntity<T> implements IdObject {

    @ShadowVersion
    public IdProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @OverwriteVersion
    @ModifySignature("saveAdditional")
    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        NBTUtil.saveId(nbt, this);
    }

    @OverwriteVersion
    @ModifySignature("load")
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        loadLogic(this, nbt);
        super.loadAdditional(nbt, registries);

    }

    @ShadowVersion
    public static void loadLogic(IdProxyBlockEntity<?> be, CompoundTag nbt);

}
