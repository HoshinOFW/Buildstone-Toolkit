package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BlockPosSupplier {

    @Nullable
    BlockPos getAsBlockPos();

}
