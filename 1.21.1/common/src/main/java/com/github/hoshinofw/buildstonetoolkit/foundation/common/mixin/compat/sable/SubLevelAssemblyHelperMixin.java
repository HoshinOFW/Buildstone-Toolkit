package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable.SableUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.platform.SableAssemblyPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SubLevelAssemblyHelper.class)
public class SubLevelAssemblyHelperMixin {

    @WrapOperation(method = "moveBlocks",
            at = @At(value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/platform/SableAssemblyPlatform;setIgnoreOnPlace(Lnet/minecraft/world/level/Level;Z)V",
                    ordinal = 0))
    private static void buildstonetoolkit$retargetAfterFirstSetIgnoreOnPlace(
            SableAssemblyPlatform instance, Level resultingLevel, boolean ignore,
            Operation<Void> original,
            @Local(argsOnly = true) ServerLevel level,
            @Local(argsOnly = true) SubLevelAssemblyHelper.AssemblyTransform transform,
            @Local(argsOnly = true) Iterable<BlockPos> blocks
    ) {
        original.call(instance, resultingLevel, ignore);
        SableUtil.targetMovedBulk(level, transform, blocks);
    }

}