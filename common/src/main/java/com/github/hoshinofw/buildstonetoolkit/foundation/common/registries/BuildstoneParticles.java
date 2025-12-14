package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.content.common.particles.TargetBlockParticle.BlockParticleOptions;
import com.github.hoshinofw.buildstonetoolkit.content.common.particles.TargetBlockParticle.BlockParticleType;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class BuildstoneParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.PARTICLE_TYPE);

    public static final Supplier<BlockParticleType<BlockParticleOptions>> BLOCK = PARTICLE_TYPES.register("block",
            () -> new BlockParticleType<>(false));

    public static void register() {
        PARTICLE_TYPES.register();
    }

}
