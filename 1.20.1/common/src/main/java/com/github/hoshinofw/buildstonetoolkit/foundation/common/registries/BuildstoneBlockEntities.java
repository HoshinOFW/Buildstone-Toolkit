package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.DummyBE;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.function.Supplier;

public class BuildstoneBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final ArrayList<Supplier<? extends BlockEntityType<? extends ProxyBlockEntity<?, ?>>>> PROXY_TYPES = new ArrayList<>();

    public static void register() {
        BLOCK_ENTITIES.register();
    }


    private static <V extends BlockEntity> RegistrySupplier<BlockEntityType<V>> registerBE(String registryName, BlockEntityType.BlockEntitySupplier<V> beSupplier, RegistrySupplier<? extends Block> blockHolder) {
        return BLOCK_ENTITIES.register(registryName, () ->
                BlockEntityType.Builder.of(beSupplier, blockHolder.get()).build(null));
    }

    private static <V extends ProxyBlockEntity<?, ?>> RegistrySupplier<BlockEntityType<V>> registerProxyBE(String registryName, BlockEntityType.BlockEntitySupplier<V> beSupplier, RegistrySupplier<? extends Block> blockHolder) {
        RegistrySupplier<BlockEntityType<V>> sup = registerBE(registryName, beSupplier, blockHolder);
        registerProxyType(sup);
        return sup;
    }

    public static void registerProxyType(Supplier<? extends BlockEntityType<? extends ProxyBlockEntity<?, ?>>> type) {
        PROXY_TYPES.add(type);
    }

    public static final RegistrySupplier<BlockEntityType<PistonProxyBlockEntity>> PISTON_PROXY = registerProxyBE("piston_proxy", PistonProxyBlockEntity::new, BuildstoneBlocks.PISTON_PROXY);
    public static final RegistrySupplier<BlockEntityType<RedstoneProxyBlockEntity>> REDSTONE_PROXY = registerProxyBE("redstone_proxy", RedstoneProxyBlockEntity::new, BuildstoneBlocks.REDSTONE_PROXY);
    public static final RegistrySupplier<BlockEntityType<ObserverProxyBlockEntity>> OBSERVER_PROXY = registerProxyBE("observer_proxy", ObserverProxyBlockEntity::new, BuildstoneBlocks.OBSERVER_PROXY);
    public static final RegistrySupplier<BlockEntityType<InteractionProxyBlockEntity>> INTERACTION_PROXY = registerProxyBE("interaction_proxy", InteractionProxyBlockEntity::new, BuildstoneBlocks.INTERACTION_PROXY);
    public static final RegistrySupplier<BlockEntityType<VisionProxyBlockEntity>> VISION_PROXY = registerProxyBE("vision_proxy", VisionProxyBlockEntity::new, BuildstoneBlocks.VISION_PROXY);

    public static ArrayList<Supplier<? extends BlockEntityType<? extends ProxyBlockEntity<?, ?>>>> getProxyTypes() {
        return PROXY_TYPES;
    }

    //Block has been removed...
    public static final RegistrySupplier<BlockEntityType<DummyBE>> GUIDED_PROXY = registerBE("directional_proxy", DummyBE::new, BuildstoneBlocks.GUIDED_PROXY);
}
