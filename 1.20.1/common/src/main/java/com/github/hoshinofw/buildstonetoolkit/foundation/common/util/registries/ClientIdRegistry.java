package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

public class ClientIdRegistry<T extends IdObject> implements IdRegistry<T>{

    private static final String name = "ClientIdRegistry";

    private final Long2ObjectOpenHashMap<T> map = new Long2ObjectOpenHashMap<>();

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Long2ObjectOpenHashMap<T> getMap() {
        return map;
    }
}
