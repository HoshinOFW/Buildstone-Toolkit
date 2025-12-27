package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.pistonproxy.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PistonMovingBlockEntityMixinInterface;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @Inject(method = "moveBlocks",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void buildstonetoolkit$afterSetBlockEntity(Level level, BlockPos blockPos, Direction direction, boolean extending,
            CallbackInfoReturnable<Boolean> cir,
            @Local(ordinal = 2) BlockPos currentFinalPos) {

        // Inside the MBE creation loop.
        BlockPos originalPos = currentFinalPos.relative(Util.resolveDirection(direction, extending).getOpposite());
        BlockEntity originalBE = level.getBlockEntity(originalPos);
        CompoundTag nbt = null;

        if (originalBE != null) {
            nbt = originalBE.saveWithoutMetadata();
            //BuildstoneToolkit.LOGGER.info("Captured NBT for block at {}: {}", originalPos, nbt);
        }

        BlockEntity newBE = level.getBlockEntity(currentFinalPos);
        if (newBE instanceof PistonMovingBlockEntity mbe && nbt != null) {
            ((PistonMovingBlockEntityMixinInterface) mbe).buildstonetoolkit$setProxyTag(nbt);
            //BuildstoneToolkit.LOGGER.info("Attached NBT to MovingPistonBlockEntity at {}", currentFinalPos);
        }
    }

    @ModifyReturnValue(
            method = "isPushable(Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/world/level/Level;" +
                    "Lnet/minecraft/core/BlockPos;" +
                    "Lnet/minecraft/core/Direction;" +
                    "ZLnet/minecraft/core/Direction;)Z",
            at = @At("RETURN")
    )
    private static boolean modifyPushableVar(boolean original, BlockState state) {
        return original || Util.isPushableBlockEntity(state);
    }

}
