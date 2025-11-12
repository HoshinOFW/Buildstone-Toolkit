package com.github.hoshinofw.buildstonetoolkit.util.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface PistonMovingBlockEntityMixinInterface {
    CompoundTag buildstonetoolkit$getProxyTag();
    void buildstonetoolkit$setProxyTag(CompoundTag tag);

    public float buildstonetoolkit$getProgressO();
}
