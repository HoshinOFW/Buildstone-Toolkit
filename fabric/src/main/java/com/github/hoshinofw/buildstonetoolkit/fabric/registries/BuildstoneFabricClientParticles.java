package com.github.hoshinofw.buildstonetoolkit.fabric.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

@Environment(EnvType.CLIENT)
public class BuildstoneFabricClientParticles {

    public static final ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

    public static void register() {
        registry.register(BuildstoneParticles.BLOCK.get(),
                spriteSet -> {
                    Sprites.BLOCK_PARTICLE_SPRITES = spriteSet;
                    return new CubeParticle.Provider(spriteSet);
                });
    }

}
