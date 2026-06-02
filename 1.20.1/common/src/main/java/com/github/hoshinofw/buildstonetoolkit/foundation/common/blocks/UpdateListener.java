package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface UpdateListener {

     void targetUpdated(UpdateListenerProxyBlockEntity<?, ?> proxyBlockEntity, @NotNull Level level);

}
