package com.github.hoshinofw.buildstonetoolkit.forge.registries;


import com.github.hoshinofw.buildstonetoolkit.foundation.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;


public class BuildstoneForgeClientEvents {

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
