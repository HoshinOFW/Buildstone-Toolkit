package com.hoshin.buildstonetoolkit.fabric.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.content.common.particles.TargetBlockParticle.BlockParticle;
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
                    return new BlockParticle.Provider(spriteSet);
                });
    }

}
