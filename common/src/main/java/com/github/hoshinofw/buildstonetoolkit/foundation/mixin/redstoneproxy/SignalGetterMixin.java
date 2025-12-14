package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.redstoneproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        if (!RedstoneProxyBlockEntity.getRegistryStatic().isTargeted(blockPos)) return;

        BlockState blockState = ((SignalGetter)this).getBlockState(blockPos);
        boolean isConductor = blockState.isRedstoneConductor((SignalGetter)this, blockPos);
        if (!isConductor) return;

        Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistryStatic().getProxiesTargeting(blockPos);
        int best = 0;
        for (RedstoneProxyBlockEntity rpbe : rpbeCollection) {
            int signal = rpbe.getSignal();
            if (signal == 15) {
                cir.setReturnValue(15);
                cir.cancel();
                return;
            }
            if (signal > best) best = signal;
        }
        int i = blockState.getSignal((SignalGetter)this, blockPos, direction);
        cir.setReturnValue(Util.max(i, best, ((SignalGetter)this).getDirectSignalTo(blockPos)));
        cir.cancel();
    }
    
    @Inject(method = "hasNeighborSignal", at = @At("HEAD"), cancellable = true)
    private void hasNeighborSignal(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (!RedstoneProxyBlockEntity.getRegistryStatic().isTargeted(blockPos)) return;

        Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistryStatic().getProxiesTargeting(blockPos);
        for (RedstoneProxyBlockEntity rpbe : rpbeCollection) {
            int signal = rpbe.getSignal();
            if (signal != 0) {
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }
        }
    }
    
}
