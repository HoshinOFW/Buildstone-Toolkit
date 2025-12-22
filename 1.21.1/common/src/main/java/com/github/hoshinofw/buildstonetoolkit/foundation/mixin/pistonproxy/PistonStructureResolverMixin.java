package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
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

    @Inject(method = "addBlockLine", at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z",
                    shift = At.Shift.BEFORE ),
            cancellable = true)

    private void onAddBlockLine(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.level.getBlockState(blockPos);

        if ((blockState.getBlock() instanceof PistonProxyBlock pistonProxy)) {
            if (pistonProxy.shouldTransferMovement(blockPos, blockState, direction)) {
                BlockPos linkPos = pistonProxy.getLinkedAbsPos(this.level, blockPos);
                if (linkPos != null) {
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
}
