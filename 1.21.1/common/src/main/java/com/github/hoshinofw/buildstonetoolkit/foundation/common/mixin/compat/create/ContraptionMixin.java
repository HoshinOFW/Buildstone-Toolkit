package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.create;

import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ModifyClass
@Mixin(Contraption.class)
public class ContraptionMixin {

    @Inject(method = "writeNBT", at = @At("RETURN"), remap = false)
    @ModifySignature("writeParkedArrays")
    @ShadowVersion
    private void writeParkedArrays(HolderLookup.Provider registries, boolean spawnPacket,
                                   CallbackInfoReturnable<CompoundTag> cir, @Local(ordinal = 0) CompoundTag nbt);

}
