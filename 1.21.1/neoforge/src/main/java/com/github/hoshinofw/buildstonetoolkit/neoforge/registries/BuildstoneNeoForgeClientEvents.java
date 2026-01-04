package com.github.hoshinofw.buildstonetoolkit.neoforge.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = BuildstoneToolkit.MOD_ID, value = Dist.CLIENT)
public class BuildstoneNeoForgeClientEvents {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BuildstoneParticles.BLOCK.get(),
                (spriteSet) -> {
                    Sprites.BLOCK_PARTICLE_SPRITES = spriteSet;
                    BuildstoneToolkit.LOGGER.info("Registering sprites: {}", spriteSet);
                    return new CubeParticle.Provider(spriteSet);
                }
                );
    }

}