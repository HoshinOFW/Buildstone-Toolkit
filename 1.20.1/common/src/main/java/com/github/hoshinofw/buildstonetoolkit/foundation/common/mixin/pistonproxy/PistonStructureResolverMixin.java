package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {

    @Shadow @Final
    private Level level;

    @Shadow @Final
    private Direction pushDirection;

    @Shadow @Final
    private BlockPos pistonPos;

    @Shadow
    protected abstract boolean addBlockLine(BlockPos blockPos, Direction direction);

    //TODO Try to capture the blockState as a local, I tried a couple times but the local won't resolve.
    @Inject(method = "addBlockLine", at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 3,
                    shift = At.Shift.AFTER ),
            cancellable = true)
    private void onAddBlockLine(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.level.getBlockState(blockPos);

        if ((blockState.getBlock() instanceof PistonProxyBlock pistonProxy)) {
            if (pistonProxy.shouldTransferMovement(blockPos, blockState, direction)) {
                BlockPos linkPos = pistonProxy.getLinkedAbsPos(this.level, blockPos);
                if (linkPos.asLong() != blockPos.asLong()) {
                    if (!this.addBlockLine(linkPos, direction)) {
                        cir.setReturnValue(false);
                        cir.cancel();
                    }
                }

            }
        }
    }
}
