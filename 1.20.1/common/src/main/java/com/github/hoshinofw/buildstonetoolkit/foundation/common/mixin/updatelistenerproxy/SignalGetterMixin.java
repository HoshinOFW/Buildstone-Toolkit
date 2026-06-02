package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.updatelistenerproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.UpdateUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;

import static com.github.hoshinofw.buildstonetoolkit.foundation.common.util.UpdateUtil.DISABLE_GET_SIGNAL_WRAPPER;

@Mixin(value = SignalGetter.class, remap = false)
public interface SignalGetterMixin extends BlockGetter {

    @WrapMethod(method = "getSignal",
            remap = true)
    private int wrapGetSignal(BlockPos queriedBlockPos, Direction direction, Operation<Integer> original) {
        int[] disable = DISABLE_GET_SIGNAL_WRAPPER.get();
        if (disable[0] > 0) {
            disable[0]--;
            try {
                return original.call(queriedBlockPos, direction);
            } finally {
                disable[0]++;
            }
        }

        long queriedPos = queriedBlockPos.asLong();

        int originalSignal = original.call(queriedBlockPos, direction);
        if (originalSignal >= 15) return 15;

        int best = originalSignal;

        if (((Object) this) instanceof ServerLevel serverLevel) {
            ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(serverLevel);

            if (direction != null) {
                //If direction is not null, that means querier can be resolved.
                // Therefore if it is asking: Am I being powered from this direction?
                // A proxy targeting the querier with face == ALL or face == direction should transmit
                // direction == null -> querier can't be resolved. This can be used intentionally.
                // I'm writing this comment bc this is weird asf and I will forget what all this does and why later.
                long queryingPos = Util.fastRelative(queriedPos, direction.getOpposite());

                if (!registry.targetIndex.contains(queryingPos, queriedPos)) return originalSignal;

                best = Math.max(UpdateUtil.getBestTransmittedSignal(queryingPos, 15, registry, direction, true), best);

                if (best >= 15) return 15;

            } else if (!registry.targetIndex.contains(queriedPos)) return originalSignal;

            best = Math.max(UpdateUtil.getBestTransmittedSignal(queriedPos, 15, registry, null, false), best);

            return best;
        }
        return originalSignal;
    }

    @WrapMethod(method = "getBestNeighborSignal",
            remap = true)
    private int wrapGetBestNeighborSignal(BlockPos pos, Operation<Integer> original) {
        int[] disable_get_signal_wrapper = DISABLE_GET_SIGNAL_WRAPPER.get();
        disable_get_signal_wrapper[0]++;
        try {
            int originalSignal = original.call(pos);

            if (((Object) this) instanceof ServerLevel serverLevel) {

                ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(serverLevel);

                if (!registry.targetIndex.containsSelfOrNeighbors(pos.asLong())) return originalSignal;

                long[] blocks = UpdateUtil.packedNeighbors(pos, true);

                return Math.max(originalSignal, UpdateUtil.getBestTransmittedSignalFromBlocks(blocks, pos.asLong(), 15, registry));

            }
            return originalSignal;

        } finally {
            disable_get_signal_wrapper[0]--;
        }
    }

    @WrapMethod(method = "hasNeighborSignal", remap = true)
    private boolean wrapHasNeighborSignal(BlockPos pos, Operation<Boolean> original) {
        int[] disable_get_signal_wrapper = DISABLE_GET_SIGNAL_WRAPPER.get();
        disable_get_signal_wrapper[0]++;
        try {
            boolean originalValue = original.call(pos);
            if (originalValue) return true;

            if (((Object) this) instanceof ServerLevel serverLevel) {

                ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(serverLevel);

                if (!registry.targetIndex.containsSelfOrNeighbors(pos.asLong())) return false;

                long[] blocks = UpdateUtil.packedNeighbors(pos, true);

                return UpdateUtil.getBestTransmittedSignalFromBlocks(blocks, pos.asLong(), 1, registry) > 0;

            }
            return false;

        } finally {
            disable_get_signal_wrapper[0]--;
        }
    }

}
