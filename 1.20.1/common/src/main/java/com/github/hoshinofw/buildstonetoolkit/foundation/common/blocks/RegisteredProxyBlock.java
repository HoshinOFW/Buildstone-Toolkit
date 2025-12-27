package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.RegisteredProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.storage.unstable.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class RegisteredProxyBlock<T extends RegisteredProxyBlockEntity<T>> extends IdProxyBlock<T> {

    public RegisteredProxyBlock(Properties properties, Class<T> rpbeClass) {
        super(properties, rpbeClass);
    }

    protected abstract @NotNull Map<ClientLevel, ProxyRegistry<T>> getClientRegistryMap();
    protected abstract @NotNull Map<ServerLevel, ProxyRegistry<T>> getServerRegistryMap();

    @Nullable
    public ProxyRegistry<T> getRegistry(Level level) {
        if (level instanceof ServerLevel serverLevel)
            return getServerRegistryMap().computeIfAbsent(serverLevel, (serverLevelKey) -> new ProxyRegistry<>(ProxyIdStorage.getIdRegistry(serverLevelKey), beclass));
        else if (level instanceof ClientLevel clientLevel) {
            return getClientRegistryMap().computeIfAbsent(clientLevel, (clientLevelKey) -> new ProxyRegistry<>(ProxyIdStorage.getIdRegistry(clientLevelKey), beclass));
        }
        return null;
    }

}
