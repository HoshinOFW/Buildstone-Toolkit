package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.pistonproxy.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PistonMovingBlockEntityMixinInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityMixinInterface{

    //Proxy tag persistence over reloading.
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$saveAdditional(CompoundTag tag, CallbackInfo ci) {
        if (this.buildstonetoolkit$getProxyTag() != null) {
            tag.put("BuildstoneProxyTag", this.buildstonetoolkit$getProxyTag().copy());
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void buildstonetoolkit$load(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("BuildstoneProxyTag", CompoundTag.TAG_COMPOUND)) {
            this.buildstonetoolkit$setProxyTag(tag.getCompound("BuildstoneProxyTag").copy());
        } else {
            this.buildstonetoolkit$setProxyTag(null);
        }
    }
}
