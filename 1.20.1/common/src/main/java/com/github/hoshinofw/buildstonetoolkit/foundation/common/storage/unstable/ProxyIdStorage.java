package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.ClientIdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.unstable.ServerIdRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ProxyIdStorage{

    public static final String DATA_NAME = "buildstonetoolkit-ServerIdRegistry";
    private static final IdRegistry<IdProxyBlockEntity<?>> clientRegistry = new ClientIdRegistry<>();

    public static <V extends IdObject> ServerIdRegistry<V> getServerIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ServerIdRegistry::load,
                ServerIdRegistry::new,
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
