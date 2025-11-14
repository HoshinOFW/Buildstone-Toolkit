package com.github.hoshinofw.buildstonetoolkit.forge.registries;

import com.github.hoshinofw.buildstonetoolkit.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle.BlockParticle;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildstoneToolkit.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BuildstoneForgeClientEvents {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BuildstoneParticles.BLOCK.get(),
                (spriteSet) -> {
                    Sprites.BLOCK_PARTICLE_SPRITES = spriteSet;
                    BuildstoneToolkit.LOGGER.info("Registering sprites: {}", spriteSet);
                    return new BlockParticle.Provider(spriteSet);
                }
        );
    }

}
