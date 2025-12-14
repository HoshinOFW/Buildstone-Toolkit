package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;

public abstract class IdProxyBlock<T extends IdProxyBlockEntity<T>> extends ProxyEntityBlock<T>{
    public IdProxyBlock(Properties properties) {
        super(properties);
    }
}
