package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.updatelistenerproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.UpdateUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

import static net.minecraft.world.level.SignalGetter.DIRECTIONS;

@Mixin(value = SignalGetter.class, remap = false)
public interface SignalGetterMixin {

    @WrapMethod(method = "getSignal")
    default int wrapGetSignal(BlockPos blockPos, Direction direction, Operation<Integer> original) {
        int originalResult = original.call(blockPos, direction);
        if (originalResult >= 15) return 15;

        BlockState blockState = ((SignalGetter)this).getBlockState(blockPos);

        if (((Object)this) instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return originalResult;

            if (!blockState.isRedstoneConductor(serverLevel, blockPos) || blockState.hasAnalogOutputSignal()) return originalResult;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);
            int bestFromProxies = UpdateUtil.getBestSignalFromRedstoneProxies(rpbeCollection);
            if (bestFromProxies >= 15) {
                return 15;
            }
            return Math.max(originalResult, bestFromProxies);
        }
        return originalResult;
    }

    @Shadow
    default int getSignal(BlockPos blockPos, Direction direction) {
        return 0;
    }

    @WrapMethod(method = "hasNeighborSignal")
    default boolean wrapHasNeighborSignal(BlockPos blockPos, Operation<Boolean> original) {
        boolean originResult = original.call(blockPos);
        if (originResult) return true;

        if (((Object)this) instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return false;

            if (serverLevel.getBlockState(blockPos).hasAnalogOutputSignal()) return false;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);

            return UpdateUtil.hasSignalFromRedstoneProxies(rpbeCollection);
        }
        return false;
    }

    @WrapMethod(method = "getBestNeighborSignal")
    default int wrapGetBestNeighborSignal(BlockPos blockPos, Operation<Integer> original) {
        int originalResult = original.call(blockPos);

        if (((Object)this) instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return originalResult;

            if (serverLevel.getBlockState(blockPos).hasAnalogOutputSignal()) return originalResult;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);
            int bestFromProxies = UpdateUtil.getBestSignalFromRedstoneProxies(rpbeCollection);

            return Math.max(originalResult, bestFromProxies);
        }

        return originalResult;

    }


    
}
