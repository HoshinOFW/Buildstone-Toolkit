package com.github.hoshinofw.buildstonetoolkit.core;


import com.github.hoshinofw.buildstonetoolkit.common.level.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.registries.events.BuildstoneClientEvents;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.resources.ResourceLocation;


public class BuildstoneToolkitClient{

    public static void init() {
        BuildstoneClientEvents.register();

        ItemPropertiesRegistry.register(
                BuildstoneItems.MOD_WAND.get(),
                new ResourceLocation(BuildstoneToolkit.MOD_ID, "proxy_mode"),
                (stack, level, entity, seed) -> ModWand.getClientMode().ordinal()
        );
    }
}
