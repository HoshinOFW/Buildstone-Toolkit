package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders.ProxyRegistryHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public class LevelMixin implements ProxyRegistryHolder {

    @Unique
    @Nullable
    private ProxyRegistry<ProxyBlockEntity<?, ?>> buildstonetoolkit$proxyRegistry = null;

    @Unique
    @Override
    public @NotNull ProxyRegistry<ProxyBlockEntity<?, ?>> buildstonetoolkit$getProxyRegistry() {
        Level level = (Level) (Object) this;
        if (buildstonetoolkit$proxyRegistry == null) {
            if (level instanceof ServerLevel serverLevel) {
                buildstonetoolkit$proxyRegistry = new ProxyRegistry<>(ProxyIdStorage.getIdRegistry(serverLevel), ProxyIdStorage.getTargetIdRegistry(serverLevel));
            } else {
                buildstonetoolkit$proxyRegistry = new ProxyRegistry<>(ProxyIdStorage.getIdRegistry(level), null);
            }
        }
        return buildstonetoolkit$proxyRegistry;
    }
}
