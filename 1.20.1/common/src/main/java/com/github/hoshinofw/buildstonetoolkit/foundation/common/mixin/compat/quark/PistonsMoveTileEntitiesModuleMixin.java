package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.quark;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.quark.QuarkPistonCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.automation.module.PistonsMoveTileEntitiesModule;

@Mixin(value = PistonsMoveTileEntitiesModule.class, remap = false)
public class PistonsMoveTileEntitiesModuleMixin {
    
    @WrapOperation(method = "detachTileEntities",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)" +
                            "Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    remap = true))
    private static BlockEntity buildstonetoolkit$skipPistonProxies(Level world, BlockPos pos, Operation<BlockEntity> original) {
        if (world.getBlockState(pos).getBlock() instanceof PistonProxyBlock) return null;
        return original.call(world, pos);
    }
    
    @WrapOperation(method = "detachTileEntities",
            at = @At(value = "INVOKE",
                    target = "Lorg/violetmoon/quark/content/automation/module/PistonsMoveTileEntitiesModule;" +
                            "setMovingBlockEntityData(Lnet/minecraft/world/level/Level;" +
                            "Lnet/minecraft/core/BlockPos;Lnet/minecraft/nbt/CompoundTag;)V"))
    private static void buildstonetoolkit$relinkPushedProxy(Level world, BlockPos dest, CompoundTag tag,
                                                            Operation<Void> original, @Local BlockPos pos) {
        QuarkPistonCompat.relinkPushedProxy(world, pos, tag);
        original.call(world, dest, tag);
    }
}