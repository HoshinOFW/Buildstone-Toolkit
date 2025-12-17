package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.redstoneproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.UpdateUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(value = SignalGetter.class, remap = false)
public interface SignalGetterMixin {

    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void beforeGetSignal(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (this instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return;

            BlockState blockState = ((SignalGetter)this).getBlockState(blockPos);
            boolean isConductor = blockState.isRedstoneConductor((SignalGetter)this, blockPos);
            if (!isConductor) return;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);
            int bestFromProxies = UpdateUtil.getBestSignalFromRedstoneProxies(rpbeCollection);
            if (bestFromProxies >= 15) {
                cir.setReturnValue(bestFromProxies);
                cir.cancel();
                return;
            }

            int i = blockState.getSignal((SignalGetter)this, blockPos, direction);
            cir.setReturnValue(Util.max(i, bestFromProxies, ((SignalGetter)this).getDirectSignalTo(blockPos)));
            cir.cancel();
        }
    }
    
    @Inject(method = "hasNeighborSignal", at = @At("HEAD"), cancellable = true)
    private void beforeHasNeighborSignal(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);

            if (UpdateUtil.hasSignalFromRedstoneProxies(rpbeCollection)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "getBestNeighborSignal", at = @At("TAIL"), cancellable = true)
    private void afterGetBestNeighborSignal(BlockPos blockPos, CallbackInfoReturnable<Integer> cir) {
        if (this instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);
            int bestFromProxies = UpdateUtil.getBestSignalFromRedstoneProxies(rpbeCollection);

            cir.setReturnValue(Math.max(cir.getReturnValue(), bestFromProxies));
            cir.cancel();
        }
    }
    
}
