package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public abstract class SyncedBlockEntity extends BlockEntity{

    @ShadowVersion
    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @OverwriteVersion
    @ModifySignature("getUpdateTag")
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return writeClient(new CompoundTag(), registries);
    }

    @OverwriteVersion
    @ModifySignature("handleUpdateTag")
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        readClient(tag, registries);
    }

    @OverwriteVersion
    @ModifySignature("onDataPacket")
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        readClient(tag, registries);
    }


    @OverwriteVersion
    @ModifySignature("readClient")
    public void readClient(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    // Special handling for client update packets
    @OverwriteVersion
    @ModifySignature("writeClient")
    public CompoundTag writeClient(CompoundTag tag, HolderLookup.Provider registries) {
        saveAdditional(tag, registries);
        return tag;
    }

}