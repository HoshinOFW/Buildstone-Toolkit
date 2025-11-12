package com.github.hoshinofw.buildstonetoolkit.registries;

import com.github.hoshinofw.buildstonetoolkit.common.level.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class BuildstoneItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.ITEM);

    public static final DeferredSupplier<ModWand> MOD_WAND = ITEMS.register("proxy_tuner",
            () -> {
                return new ModWand(new Item.Properties()
                        .arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)
                        .stacksTo(1));
            });

    public static final RegistrySupplier<Item> GUIDED_PROXY =
            BuildstoneItems.ITEMS.register("guided_proxy",
                    () -> new BlockItem(BuildstoneBlocks.GUIDED_PROXY.get(),
                    new Item.Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    public static final RegistrySupplier<Item> PISTON_PROXY =
            BuildstoneItems.ITEMS.register("piston_proxy",
                    () -> new BlockItem(BuildstoneBlocks.PISTON_PROXY.get(),
                    new Item.Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));

    protected static Item getModWand() {
        return MOD_WAND.get();
    }

    public static void register() {
        ITEMS.register();
    }

}
