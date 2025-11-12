package com.github.hoshinofw.buildstonetoolkit.registries;

import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class BuildstoneBlockItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> GUIDED_PROXY =
            ITEMS.register("guided_proxy", () -> new BlockItem(BuildstoneBlocks.GUIDED_PROXY.get(),
                    new Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    public static final RegistrySupplier<Item> PISTON_PROXY =
            ITEMS.register("piston_proxy", () -> new BlockItem(BuildstoneBlocks.PISTON_PROXY.get(),
                    new Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    public static void register() {
        ITEMS.register();
    }
}
