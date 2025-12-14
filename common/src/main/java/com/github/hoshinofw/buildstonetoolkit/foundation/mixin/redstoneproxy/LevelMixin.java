package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.redstoneproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.UpdateUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin {

    @Inject(method = "updateNeighbourForOutputSignal", at = @At("HEAD"))
    public void beforeUpdateNeighbourForOutputSignal(BlockPos blockPos, Block block, CallbackInfo ci) {
        UpdateUtil.doTargetUpdatedLogic(blockPos);
    }

}
