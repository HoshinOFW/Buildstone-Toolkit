package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public abstract class SyncedBlockEntity extends BlockEntity{

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return writeClient(new CompoundTag());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        readClient(tag);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        readClient(tag);
    }

    // Special handling for client update packets
    public void readClient(CompoundTag tag) {
        load(tag);
    }

    // Special handling for client update packets
    public CompoundTag writeClient(CompoundTag tag) {
        saveAdditional(tag);
        return tag;
    }

    public void sendData() {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(getBlockPos());
    }

    public void notifyUpdate() {
        setChanged();
        sendData();
    }

    public HolderGetter<Block> blockHolderGetter() {
        return level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
    }

}