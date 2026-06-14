package com.github.hoshinofw.buildstonetoolkit.foundation.client.core;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.events.BuildstoneClientEvents;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.ProxyIdStorage;
import dev.architectury.platform.Platform;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.multiplayer.ClientLevel;

public class BuildstoneToolkitClient{

    public static void init() {
        BuildstoneClientEvents.register();

        ItemPropertiesRegistry.register(
                BuildstoneItems.MOD_WAND.get(),
                ModWand.MOD_WAND_PROXY_MODE,
                (stack, level, entity, seed) -> ModWand.getClientMode().ordinal()
        );

        if (Platform.isModLoaded("sable")) {sableCompatPostInit();}
    }

    public static void sableCompatPostInit() {

    }

    public static void onClientLevelLoad(ClientLevel clientLevel) {
    }

    public static void onClientLevelUnload(ClientLevel clientLevel) {
        ProxyIdStorage.getClientRegistry().clear();

    }
}
