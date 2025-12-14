package com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public interface PlayerMixinInterface {
    public @Nullable BlockPos buildstonetoolkit$getSelectedBlockPos();
    public void buildstonetoolkit$setSelectedBlockPos(BlockPos pos);

}
