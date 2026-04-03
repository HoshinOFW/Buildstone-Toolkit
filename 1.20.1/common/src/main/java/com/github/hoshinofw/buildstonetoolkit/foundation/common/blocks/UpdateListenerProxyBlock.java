package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.ProxyRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class UpdateListenerProxyBlock extends RegisteredProxyBlock<UpdateListenerProxyBlockEntity> implements UpdateListener<UpdateListenerProxyBlockEntity>{
    public UpdateListenerProxyBlock(Properties properties) {
        super(properties, UpdateListenerProxyBlockEntity.class);
    }

    private static final Map<Level, ProxyRegistry<UpdateListenerProxyBlockEntity>> serverRegistryMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Level, ProxyRegistry<UpdateListenerProxyBlockEntity>> clientRegistryMap = new Object2ObjectOpenHashMap<>();

    @Override
    @NotNull
    protected Map<Level, ProxyRegistry<UpdateListenerProxyBlockEntity>> getClientRegistryMap() {
        return clientRegistryMap;
    }

    @Override
    @NotNull
    protected Map<Level, ProxyRegistry<UpdateListenerProxyBlockEntity>> getServerRegistryMap() {
        return serverRegistryMap;
    }

    @Override
    public abstract void targetUpdated(UpdateListenerProxyBlockEntity be, @NotNull Level level);

}
