package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class IdProxyBlock<T extends IdProxyBlockEntity<T>> extends ProxyEntityBlock<T>{
    public IdProxyBlock(Properties properties, Class<T> BEClass) {
        super(properties, BEClass);
    }

    public long getId(@NotNull Level level, BlockPos proxyPos) {
        return getBlockEntity(level, proxyPos).getId();
    }
}
