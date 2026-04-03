package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.pistonproxy;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PistonMovingBlockEntityMixinInterface;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@DeleteMethodsAndFields({"buildstonetoolkit$saveAdditional", "buildstonetoolkit$load"})
@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityMixinInterface{

    //Proxy tag persistence over reloading.
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$saveAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (this.buildstonetoolkit$getProxyTag() != null) {
            tag.put("BuildstoneProxyTag", this.buildstonetoolkit$getProxyTag().copy());
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void buildstonetoolkit$load(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (tag.contains("BuildstoneProxyTag", CompoundTag.TAG_COMPOUND)) {
            this.buildstonetoolkit$setProxyTag(tag.getCompound("BuildstoneProxyTag").copy());
        } else {
            this.buildstonetoolkit$setProxyTag(null);
        }
    }
}
