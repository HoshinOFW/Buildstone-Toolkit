package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PistonMovingBlockEntityMixinInterface;
import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ModifyClass
@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityMixinInterface{

    @OverwriteVersion
    @ModifySignature("buildstonetoolkit$saveAdditional")
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$saveAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (this.buildstonetoolkit$getProxyTag() != null) {
            tag.put("BuildstoneProxyTag", this.buildstonetoolkit$getProxyTag().copy());
        }
    }

    @OverwriteVersion
    @ModifySignature("buildstonetoolkit$load")
    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$loadAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (tag.contains("BuildstoneProxyTag", CompoundTag.TAG_COMPOUND)) {
            this.buildstonetoolkit$setProxyTag(tag.getCompound("BuildstoneProxyTag").copy());
        } else {
            this.buildstonetoolkit$setProxyTag(null);
        }
    }
}
