package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.updatelistenerproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.UpdateUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "updateNeighborsAt", at = @At("HEAD"))
    public void updateNeighborsAt(BlockPos blockPos, Block block, CallbackInfo ci) {
        @NotNull Level level = (Level)(Object)this;
        long[] positions = UpdateUtil.packedNeighbors(blockPos, true);
        ProxyBlockEntity.getRegistry(level).targetIndex.forEachInIfPresent(positions,
                (long targetPos) -> UpdateUtil.targetUpdated(targetPos, level));
    }

    @Inject(method = "updateNeighborsAtExceptFromFacing", at = @At("HEAD"))
    public void updateNeighborsAtExceptFromFacing(BlockPos blockPos, Block block, Direction direction, CallbackInfo ci) {
        @NotNull Level level = (Level)(Object)this;
        long[] positions = UpdateUtil.packedNeighborsExceptFromFacing(blockPos, true, direction);
        ProxyBlockEntity.getRegistry(level).targetIndex.forEachInIfPresent(positions,
                (long targetPos) -> UpdateUtil.targetUpdated(targetPos, level));
    }

    @Inject(method = "neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"))
    public void neighborChanged(BlockPos blockPos, Block block, BlockPos blockPos2, CallbackInfo ci) {
        UpdateUtil.updateProxiesTargeting(blockPos.asLong(), (Level)(Object)this);
    }

    @Inject(method = "neighborChanged(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V", at = @At("HEAD"))
    public void neighborChanged(BlockState blockState, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, CallbackInfo ci) {
        UpdateUtil.updateProxiesTargeting(blockPos.asLong(), (Level)(Object)this);
    }

}
