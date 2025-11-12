package com.github.hoshinofw.buildstonetoolkit.registries;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.PistonProxy;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.GuidedProxy;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BuildstoneBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> GUIDED_PROXY = BLOCKS.register(
            "guided_proxy",
            () -> new GuidedProxy(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final RegistrySupplier<Block> PISTON_PROXY = BLOCKS.register(
            "piston_proxy",
            () -> new PistonProxy(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static void register() {
        BLOCKS.register();
    }
}
