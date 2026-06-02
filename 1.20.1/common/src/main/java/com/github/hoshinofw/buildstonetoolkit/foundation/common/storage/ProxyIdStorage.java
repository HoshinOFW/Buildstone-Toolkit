package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ProxyIdStorage{

    public static final String ID_REGISTRY_DATA_NAME = "buildstonetoolkit#server_id_registry";
    private static final IdRegistry<ProxyBlockEntity<?, ?>> clientRegistry = new ClientIdRegistry<>();

    public static final String TARGET_ID_REGISTRY_DATA_NAME = "buildstonetoolkit#target_id_registry";

    public static <V extends IdObject> @NotNull ServerIdRegistry<V> getServerIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ServerIdRegistry::load,
                ServerIdRegistry::new,
                ID_REGISTRY_DATA_NAME
        );
    }

    public static @NotNull IdRegistry<ProxyBlockEntity<?, ?>> getClientRegistry() {
        return clientRegistry;
    }

    @NotNull
    public static IdRegistry<ProxyBlockEntity<?, ?>> getIdRegistry(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return getServerIdRegistry(serverLevel);
        } else {
            return getClientRegistry();
        }
    }

    public static @NotNull TargetIdRegistry getTargetIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                TargetIdRegistry::load,
                TargetIdRegistry::new,
                TARGET_ID_REGISTRY_DATA_NAME
        );
    }
}
