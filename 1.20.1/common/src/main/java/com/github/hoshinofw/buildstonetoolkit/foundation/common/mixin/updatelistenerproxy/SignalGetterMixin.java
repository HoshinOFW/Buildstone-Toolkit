package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.updatelistenerproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.UpdateUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collection;

import static net.minecraft.world.level.SignalGetter.DIRECTIONS;

@Mixin(value = SignalGetter.class, remap = false)
public interface SignalGetterMixin {

    /**
     * @author HoshinOFW
     * @reason Target is an interface, this version of mixins does not support @Inject for interface default methods. I tried, but it was not possible. Please reach out to me if you believe you can find a workaround that does not involve an overwrite.
     */
    @Overwrite(remap = true)
    default int getSignal(BlockPos blockPos, Direction direction) {
        BlockState blockState = ((SignalGetter)this).getBlockState(blockPos);
        int i = blockState.getSignal(((SignalGetter)this), blockPos, direction);
        int u = blockState.isRedstoneConductor(((SignalGetter)this), blockPos) ? Math.max(i, ((SignalGetter)this).getDirectSignalTo(blockPos)) : i;

        if (((Object)this) instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return u;

            if (!blockState.isRedstoneConductor(serverLevel, blockPos) || blockState.hasAnalogOutputSignal()) return u;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);
            int bestFromProxies = UpdateUtil.getBestSignalFromRedstoneProxies(rpbeCollection);
            if (bestFromProxies >= 15) {
                return 15;
            }
            return Math.max(u, bestFromProxies);
        }
        return u;
    }

    /**
     * @author HoshinOFW
     * @reason Target is an interface, this version of mixins does not support @Inject for interface default methods. I tried, but it was not possible. Please reach out to me if you believe you can find a workaround that does not involve an overwrite.
     */
    @Overwrite(remap = true)
    default boolean hasNeighborSignal(BlockPos blockPos) {
        if (this.getSignal(blockPos.below(), Direction.DOWN) > 0) {
            return true;
        } else if (this.getSignal(blockPos.above(), Direction.UP) > 0) {
            return true;
        } else if (this.getSignal(blockPos.north(), Direction.NORTH) > 0) {
            return true;
        } else if (this.getSignal(blockPos.south(), Direction.SOUTH) > 0) {
            return true;
        } else if (this.getSignal(blockPos.west(), Direction.WEST) > 0) {
            return true;
        } else if (this.getSignal(blockPos.east(), Direction.EAST) > 0) {
            return true;
        }
        if (((Object)this) instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return false;

            if (serverLevel.getBlockState(blockPos).hasAnalogOutputSignal()) return false;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);

            return UpdateUtil.hasSignalFromRedstoneProxies(rpbeCollection);
        }
        return false;
    }

    /**
     * @author HoshinOFW
     * @reason Target is an interface, this version of mixins does not support @Inject for interface default methods. I tried, but it was not possible. Please reach out to me if you believe you can find a workaround that does not involve an overwrite.
     */
    @Overwrite(remap = true)
    default int getBestNeighborSignal(BlockPos blockPos) {
        int i = 0;
        for(Direction direction : DIRECTIONS) {
            int j = this.getSignal(blockPos.relative(direction), direction);
            if (j >= 15) {
                return 15;
            }
            if (j > i) {
                i = j;
            }
        }

        if (((Object)this) instanceof ServerLevel serverLevel) {
            if (!RedstoneProxyBlockEntity.getRegistry(serverLevel).isTargeted(blockPos)) return i;

            if (serverLevel.getBlockState(blockPos).hasAnalogOutputSignal()) return i;

            Collection<RedstoneProxyBlockEntity> rpbeCollection = RedstoneProxyBlockEntity.getRegistry(serverLevel).getProxiesTargeting(blockPos, RedstoneProxyBlockEntity.class);
            int bestFromProxies = UpdateUtil.getBestSignalFromRedstoneProxies(rpbeCollection);

            return Math.max(i, bestFromProxies);
        }
        return i;

    }


    
}
