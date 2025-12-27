package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.IdProxyBlockEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface UpdateListener<T extends IdProxyBlockEntity<T>> {

    public void targetUpdated(T idProxyBlockEntity, @NotNull Level level);

}
