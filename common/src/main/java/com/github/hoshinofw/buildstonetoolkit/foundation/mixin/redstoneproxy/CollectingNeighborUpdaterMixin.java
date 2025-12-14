package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.redstoneproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.UpdateUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CollectingNeighborUpdater.class)
public class CollectingNeighborUpdaterMixin {

    @Inject(method = "neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V",
    at = @At("HEAD"))
    public void neighborChanged(BlockPos blockPos, Block block, BlockPos blockPos2, CallbackInfo ci) {
        UpdateUtil.doTargetUpdatedLogic(blockPos);
    }
    @Inject(method = "neighborChanged(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V",
    at = @At("HEAD"))
    public void neighborChanged(BlockState blockState, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, CallbackInfo ci) {
        UpdateUtil.doTargetUpdatedLogic(blockPos);
    }

}
