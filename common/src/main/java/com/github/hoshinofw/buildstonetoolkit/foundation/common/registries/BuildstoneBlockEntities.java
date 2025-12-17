package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.ObserverProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.PistonProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.DummyBE;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BuildstoneBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PistonProxyBlockEntity>> PISTON_PROXY =
            BLOCK_ENTITIES.register("piston_proxy", () ->
                    BlockEntityType.Builder.of(PistonProxyBlockEntity::new, BuildstoneBlocks.PISTON_PROXY.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<?>> GUIDED_PROXY =
            BLOCK_ENTITIES.register("directional_proxy", () ->
                    BlockEntityType.Builder.of(DummyBE::new, BuildstoneBlocks.GUIDED_PROXY.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<RedstoneProxyBlockEntity>> REDSTONE_PROXY =
            BLOCK_ENTITIES.register("redstone_proxy", () ->
                    BlockEntityType.Builder.of(RedstoneProxyBlockEntity::new, BuildstoneBlocks.REDSTONE_PROXY.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ObserverProxyBlockEntity>> OBSERVER_PROXY =
            BLOCK_ENTITIES.register("observer_proxy", () ->
                    BlockEntityType.Builder.of(ObserverProxyBlockEntity::new, BuildstoneBlocks.OBSERVER_PROXY.get()).build(null));

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
