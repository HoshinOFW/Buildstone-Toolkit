package com.github.hoshinofw.buildstonetoolkit.registries;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.entity.PistonProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.entity.GuidedProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BuildstoneBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PistonProxyBlockEntity>> PISTON_PROXY =
            BLOCK_ENTITIES.register("piston_proxy", () ->
                BlockEntityType.Builder.of(PistonProxyBlockEntity::new, BuildstoneBlocks.PISTON_PROXY.get()).build(null)
            );

    public static final RegistrySupplier<BlockEntityType<GuidedProxyBlockEntity>> GUIDED_PROXY =
            BLOCK_ENTITIES.register("directional_proxy", () ->
                    BlockEntityType.Builder.of(GuidedProxyBlockEntity::new, BuildstoneBlocks.GUIDED_PROXY.get()).build(null)
            );

    public static void register() {
        BLOCK_ENTITIES.register();
    }
}
