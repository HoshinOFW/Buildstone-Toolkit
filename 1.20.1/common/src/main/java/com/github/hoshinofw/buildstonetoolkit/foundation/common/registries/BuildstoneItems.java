package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.extensions.injected.InjectedItemPropertiesExtension;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BuildstoneItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.ITEM);

    public static final DeferredSupplier<ModWand> MOD_WAND = ITEMS.register("proxy_tuner",
            () -> new ModWand(((InjectedItemPropertiesExtension) new Item.Properties())
                    .arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)
                    .stacksTo(1)));

    public static void register() {
        ITEMS.register();
    }

    public static DeferredSupplier<Item> registerProxyItem(String registryName, RegistrySupplier<? extends Block> blockSupplier) {
        return ITEMS.register(registryName, () -> new BlockItem(blockSupplier.get(), ((InjectedItemPropertiesExtension) new Item.Properties()).arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)));
    }

    public static final DeferredSupplier<Item> PISTON_PROXY = registerProxyItem("piston_proxy", BuildstoneBlocks.PISTON_PROXY);
    public static final DeferredSupplier<Item> REDSTONE_PROXY = registerProxyItem("redstone_proxy", BuildstoneBlocks.REDSTONE_PROXY);
    public static final DeferredSupplier<Item> OBSERVER_PROXY = registerProxyItem("observer_proxy", BuildstoneBlocks.OBSERVER_PROXY);
    public static final DeferredSupplier<Item> INTERACTION_PROXY = registerProxyItem("interaction_proxy", BuildstoneBlocks.INTERACTION_PROXY);
    public static final DeferredSupplier<Item> VISION_PROXY = registerProxyItem("vision_proxy", BuildstoneBlocks.VISION_PROXY);
}
