package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class BuildstoneItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.ITEM);

    public static final DeferredSupplier<ModWand> MOD_WAND = ITEMS.register("proxy_tuner",
            () -> new ModWand(new Item.Properties()
                    .arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)
                    .stacksTo(1)));

    public static final DeferredSupplier<Item> GUIDED_PROXY =
            ITEMS.register("guided_proxy", () -> new BlockItem(BuildstoneBlocks.GUIDED_PROXY.get(),
                    new Item.Properties()));
    public static final DeferredSupplier<Item> PISTON_PROXY =
            ITEMS.register("piston_proxy", () -> new BlockItem(BuildstoneBlocks.PISTON_PROXY.get(),
                    new Item.Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    public static final DeferredSupplier<Item> REDSTONE_PROXY =
            ITEMS.register("redstone_proxy", () -> new BlockItem(BuildstoneBlocks.REDSTONE_PROXY.get(),
                    new Item.Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    public static final DeferredSupplier<Item> OBSERVER_PROXY =
            ITEMS.register("observer_proxy", () -> new BlockItem(BuildstoneBlocks.OBSERVER_PROXY.get(),
                    new Item.Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    public static void register() {
        ITEMS.register();
    }

}
