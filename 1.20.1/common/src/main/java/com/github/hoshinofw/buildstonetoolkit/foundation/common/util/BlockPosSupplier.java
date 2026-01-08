package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BlockPosSupplier {

    @Nullable
    BlockPos getAsBlockPos();

}
