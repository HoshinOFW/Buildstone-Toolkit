package com.github.hoshinofw.buildstonetoolkit.foundation.storage.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ClientIdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.unstable.ServerIdRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class ProxyIdStorage{

    public static final String DATA_NAME = "buildstonetoolkit-ServerIdRegistry";
    private static final IdRegistry<IdProxyBlockEntity<?>> clientRegistry = new ClientIdRegistry<>();

    private static <V extends IdObject> SavedData.Factory<ServerIdRegistry<V>> factory() {
        return new SavedData.Factory<>(
                ServerIdRegistry::new,
                ServerIdRegistry::load,
                null
        );
    }

    public static <V extends IdObject> ServerIdRegistry<V> getServerIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                factory(),
                DATA_NAME
        );
    }

    public static @NotNull IdRegistry<IdProxyBlockEntity<?>> getClientRegistry() {
        return clientRegistry;
    }

    @NotNull
    public static IdRegistry<IdProxyBlockEntity<?>> getIdRegistry(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return getServerIdRegistry(serverLevel);
        } else {
            return getClientRegistry();
        }
    }
}
