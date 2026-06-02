package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ServerIdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.TargetIdRegistry;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ProxyIdStorage{

    @ShadowVersion
    public static final String ID_REGISTRY_DATA_NAME;

    @ShadowVersion
    public static final String TARGET_ID_REGISTRY_DATA_NAME;


    @Contract(value = " -> new", pure = true)
    private static <V extends IdObject> SavedData.@NotNull Factory<ServerIdRegistry<V>> serverIdRegistryFactory() {
        return new SavedData.Factory<>(
                ServerIdRegistry::build,
                ServerIdRegistry::load,
                null
        );
    }

    @OverwriteVersion
    public static <V extends IdObject> @NotNull ServerIdRegistry<V> getServerIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                serverIdRegistryFactory(),
                ID_REGISTRY_DATA_NAME
        );
    }


    public static SavedData.Factory<TargetIdRegistry> serverTargetIdRegistryFactory() {
        return new SavedData.Factory<>(
                TargetIdRegistry::build,
                TargetIdRegistry::load,
                null);
    }

    @OverwriteVersion
    public static @NotNull TargetIdRegistry getTargetIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ProxyIdStorage.serverTargetIdRegistryFactory(),
                TARGET_ID_REGISTRY_DATA_NAME
        );
    }




}
