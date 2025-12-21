package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BuildstoneBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.BLOCK);

    public static void register() {
        BLOCKS.register();
    }

    public static final RegistrySupplier<PistonProxyBlock> PISTON_PROXY = BLOCKS.register(
            "piston_proxy",
            () -> new PistonProxyBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .isRedstoneConductor((state, level, pos) -> false)
            ));

    public static final RegistrySupplier<RedstoneProxyBlock> REDSTONE_PROXY = BLOCKS.register(
            "redstone_proxy",
            () -> new RedstoneProxyBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .isRedstoneConductor((state, level, pos) -> false)
            ));

    public static final RegistrySupplier<ObserverProxyBlock> OBSERVER_PROXY = BLOCKS.register(
            "observer_proxy",
            () -> new ObserverProxyBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .isRedstoneConductor((state, level, pos) -> false)
            ));

    public static final RegistrySupplier<RightClickProxyBlock> RIGHT_CLICK_PROXY = BLOCKS.register(
            "right_click_proxy",
            () -> new RightClickProxyBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .isRedstoneConductor((state, level, pos) -> false)
            ));

    public static final RegistrySupplier<LookingAtProxyBlock> LOOKING_AT_PROXY = BLOCKS.register(
            "looking_at_proxy",
            () -> new LookingAtProxyBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .isRedstoneConductor((state, level, pos) -> false)
            ));

    //Block has been removed...
    public static final RegistrySupplier<Block> GUIDED_PROXY = BLOCKS.register(
            "guided_proxy",
            () -> new AirBlock(BlockBehaviour.Properties.of()));
}
