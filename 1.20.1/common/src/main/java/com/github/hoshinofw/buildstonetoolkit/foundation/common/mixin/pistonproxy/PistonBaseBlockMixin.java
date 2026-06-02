package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.PistonUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PistonMovingBlockEntityMixinInterface;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @ModifyReturnValue(
            method = "isPushable(Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/world/level/Level;" +
                    "Lnet/minecraft/core/BlockPos;" +
                    "Lnet/minecraft/core/Direction;" +
                    "ZLnet/minecraft/core/Direction;)Z",
            at = @At("RETURN"))
    private static boolean modifyPushableVar(boolean original, BlockState state) {
        return original || PistonUtil.isPushableBlockEntity(state);
    }

    @WrapOperation(method = "moveBlocks",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;" +
                            "newMovingBlockEntity(Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/core/Direction;ZZ)" +
                            "Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    ordinal = 0))
    private BlockEntity buildstonetoolkit$tagMovedMBE(
            BlockPos newPosition, BlockState movingPistonBlockState, BlockState originalState,
            Direction movementDirection, boolean extending, boolean isSourcePiston,
            Operation<BlockEntity> original,
            Level level, BlockPos pistonPosition, Direction pistonDirection, boolean pushing) {

        BlockEntity mbe = original.call(newPosition, movingPistonBlockState, originalState, movementDirection, extending, isSourcePiston);

        if (!isSourcePiston && mbe instanceof PistonMovingBlockEntity pistonMBE) {
            Direction effective = Util.resolveDirection(pistonDirection, pushing);
            BlockPos originalPos = newPosition.relative(effective.getOpposite());
            BlockEntity originalBE = level.getBlockEntity(originalPos);
            if (originalBE != null) {
                CompoundTag nbt = NBTUtil.saveWithId(originalBE, level);
                ((PistonMovingBlockEntityMixinInterface) pistonMBE).buildstonetoolkit$setProxyTag(nbt);
            }
        }

        return mbe;
    }

}
