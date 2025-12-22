package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.PistonUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PistonMovingBlockEntityMixinInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityMixinInterface{

    @Shadow public abstract Direction getDirection();

    @Shadow public abstract boolean isExtending();

    @Unique @Nullable
    public CompoundTag buildstonetoolkit$proxyTag = null;

    @Unique @Override
    public CompoundTag buildstonetoolkit$getProxyTag() {
        return buildstonetoolkit$proxyTag;
    }

    @Unique @Override
    public void buildstonetoolkit$setProxyTag(CompoundTag tag) {
        this.buildstonetoolkit$proxyTag = tag;
    }

    @Unique @Override
    public float buildstonetoolkit$getProgressO() {
        return ((BlockEntityAccessor) this).getProgressO();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            ordinal = 1, shift = At.Shift.AFTER))
    //Server sided
    private static void postSetBlockInjectionInTick(@NotNull Level level, BlockPos finalPos, BlockState pistonMovingBlockState, PistonMovingBlockEntity mbe, CallbackInfo ci) {
        Direction moveDirection = Util.resolveDirection(mbe.getDirection(), mbe.isExtending());
        Direction fromDirection = moveDirection.getOpposite();
        BlockPos originalPos = finalPos.relative(fromDirection);

        PistonUtil.doProxyUpdatingLogic(level, mbe, moveDirection, originalPos, finalPos);
    }

    @Inject(method = "tick", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;progressO:F",
            opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    //Client sided
    private static void afterProgressUpdate(Level level, BlockPos finalPos, BlockState state, PistonMovingBlockEntity mbe, CallbackInfo ci) {
        if (level.isClientSide && ((PistonMovingBlockEntityMixinInterface)mbe).buildstonetoolkit$getProgressO() >= 1.0F) {
            Direction moveDirection = Util.resolveDirection(mbe.getDirection(), mbe.isExtending());
            Direction fromDirection = moveDirection.getOpposite();
            BlockPos originalPos = finalPos.relative(fromDirection);

            PistonUtil.doProxyUpdatingLogic(level, mbe, moveDirection, originalPos, finalPos);
        }
    }


    @Inject(method = "finalTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            ordinal = 0, shift = At.Shift.AFTER))
    //finalTick is called mostly on the client, but it is mainly to resolve MBE when something has gone wrong.
    //We inject to add our own logic to the resolution.
    private void postSetBlockInjectionInFinalTick(CallbackInfo ci) {
        Level level = ((BlockEntity)(Object) this).getLevel();
        if (level == null) {return;}
        @SuppressWarnings("ConstantConditions") BlockPos finalPos = ((BlockEntity)(Object) this).getBlockPos();
        @SuppressWarnings("ConstantConditions") PistonMovingBlockEntity mbe = ((PistonMovingBlockEntity)(Object) this);
        Direction moveDirection = Util.resolveDirection(this.getDirection(), this.isExtending());
        Direction fromDirection = moveDirection.getOpposite();
        BlockPos originalPos = finalPos.relative(fromDirection);

        PistonUtil.doProxyUpdatingLogic(level, mbe, moveDirection, originalPos, finalPos);
    }

    //Proxy tag persistence over reloading.
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$saveAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (this.buildstonetoolkit$proxyTag != null) {
            tag.put("BuildstoneProxyTag", this.buildstonetoolkit$proxyTag.copy());
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$loadAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (tag.contains("BuildstoneProxyTag", CompoundTag.TAG_COMPOUND)) {
            this.buildstonetoolkit$proxyTag = tag.getCompound("BuildstoneProxyTag").copy();
        } else {
            this.buildstonetoolkit$proxyTag = null;
        }
    }

    //Tests
    @Inject(method = "finalTick", at = @At("HEAD"))
    private void finalTick(CallbackInfo ci) {
        //BuildstoneToolkit.LOGGER.info("finalTick called");
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void testCtor(BlockPos blockPos, BlockState blockState, BlockState blockState2, Direction direction, boolean bl, boolean bl2, CallbackInfo ci) {
        //BuildstoneToolkit.LOGGER.info("PistonMovingBlockEntity created at {}", blockPos);
    }
}
