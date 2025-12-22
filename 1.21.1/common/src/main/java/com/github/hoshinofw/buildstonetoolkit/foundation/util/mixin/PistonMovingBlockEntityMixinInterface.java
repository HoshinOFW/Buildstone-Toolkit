package com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin;

import net.minecraft.nbt.CompoundTag;

public interface PistonMovingBlockEntityMixinInterface {
    CompoundTag buildstonetoolkit$getProxyTag();
    void buildstonetoolkit$setProxyTag(CompoundTag tag);

    public float buildstonetoolkit$getProgressO();
}
