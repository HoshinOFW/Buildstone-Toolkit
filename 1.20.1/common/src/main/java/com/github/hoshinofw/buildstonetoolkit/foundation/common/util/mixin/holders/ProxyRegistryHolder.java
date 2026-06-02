package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import org.jetbrains.annotations.NotNull;

public interface ProxyRegistryHolder {

    @NotNull ProxyRegistry<ProxyBlockEntity<?, ?>> buildstonetoolkit$getProxyRegistry();

    default @NotNull ProxyRegistry<ProxyBlockEntity<?, ?>> getProxyRegistry() {
        return buildstonetoolkit$getProxyRegistry();
    }

}
