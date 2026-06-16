package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders.LevelHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.zeta.piston.ZetaPistonStructureResolver;

@Mixin(ZetaPistonStructureResolver.class)
public abstract class ZetaPistonStructureResolverMixin extends PistonStructureResolver {
    @Shadow protected abstract boolean addBlockLine(BlockPos blockPos, Direction direction);

    public ZetaPistonStructureResolverMixin(Level level, BlockPos pistonPos, Direction pistonDirection, boolean extending) {
        super(level, pistonPos, pistonDirection, extending);
    }

    @Inject(method = "addBlockLine", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            ordinal = 3,
            shift = At.Shift.AFTER ),
            cancellable = true)
    private void onAddBlockLine(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Level level = ((LevelHolder)this).getLevel();
        BlockState blockState = level.getBlockState(blockPos);

        if ((blockState.getBlock() instanceof PistonProxyBlock pistonProxy)) {
            if (pistonProxy.shouldTransferMovement(blockPos, blockState, direction)) {
                BlockPos linkPos = pistonProxy.getLinkedAbsPos(level, blockPos);
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
