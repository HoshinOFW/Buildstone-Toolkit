package com.github.hoshinofw.buildstonetoolkit.foundation.common.core;


import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events.BuildstoneClientEvents;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.resources.ResourceLocation;

public class BuildstoneToolkitClient{
    public static void init() {
        BuildstoneClientEvents.register();

        //Register modWand item property
        ItemPropertiesRegistry.register(
                BuildstoneItems.MOD_WAND.get(),
                ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "proxy_mode"),
                (stack, level, entity, seed) -> ModWand.getClientMode().ordinal()
        );
    }
}
