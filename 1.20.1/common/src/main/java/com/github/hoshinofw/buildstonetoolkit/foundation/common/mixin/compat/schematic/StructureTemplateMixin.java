package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.schematic;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    @WrapOperation(method = "fillFromWorld",
            at = @At(value = "NEW",
                    target = "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;",
                    ordinal = 0))
    public StructureBlockInfo injectIntoFillFromWorld(BlockPos pos, BlockState state, CompoundTag nbt, Operation<StructureBlockInfo> original,
                                                      @Local(argsOnly = true) BlockPos blockPos,
                                                      @Local(argsOnly = true) Level level,
                                                      @Local(ordinal = 1) BlockPos blockPos2,
                                                      @Local BlockEntity blockEntity) {

        if (!(blockEntity instanceof ProxyBlockEntity<?,?> proxyBe) ||
                !Util.isWithin(proxyBe.getTargetPos(), blockPos, blockPos2)) {
            //BuildstoneToolkit.LOGGER.info("injectIntoFillFromWorld: special conditions inactive");
            return original.call(pos, state, nbt);
        }
        //BuildstoneToolkit.LOGGER.info("injectIntoFillFromWorld: special conditions active");

        boolean prev = ProxyBlockEntity.storePositionAsRelative.get();
        ProxyBlockEntity.storePositionAsRelative.set(true);
        ProxyBlockEntity.storeTargetId.set(false);
        try {
            return original.call(pos, state, NBTUtil.saveWithoutId(proxyBe, level));
        } finally {
            //BuildstoneToolkit.LOGGER.info("injectIntoFillFromWorld: finally reached!");
            ProxyBlockEntity.storePositionAsRelative.set(prev);
            ProxyBlockEntity.storeTargetId.set(true);
        }
    }


}
