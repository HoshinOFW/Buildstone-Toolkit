package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdObject;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.ServerIdRegistry;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ProxyIdStorage{

    @ShadowVersion
    public static final String DATA_NAME;

    @Contract(value = " -> new", pure = true)
    private static <V extends IdObject> SavedData.@NotNull Factory<ServerIdRegistry<V>> factory() {
        return new SavedData.Factory<>(
                ServerIdRegistry::build,
                ServerIdRegistry::load,
                null
        );
    }

    @OverwriteVersion
    public static <V extends IdObject> @NotNull ServerIdRegistry<V> getServerIdRegistry(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                factory(),
                DATA_NAME
        );
    }


}
