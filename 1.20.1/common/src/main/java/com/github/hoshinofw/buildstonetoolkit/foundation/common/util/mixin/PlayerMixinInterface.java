package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface PlayerMixinInterface {
    public @Nullable BlockPos buildstonetoolkit$getSelectedBlockPos();
    public void buildstonetoolkit$setSelectedBlockPos(BlockPos pos);

    public long buildstonetoolkit$getSelectedProxyId();
    public void buildstonetoolkit$setSelectedProxyId(long pos);

}
