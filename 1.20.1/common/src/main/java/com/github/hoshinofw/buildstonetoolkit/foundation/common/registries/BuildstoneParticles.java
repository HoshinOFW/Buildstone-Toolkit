package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.unstable.CubeParticleOptions;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.unstable.CubeParticleType;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class BuildstoneParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuildstoneToolkit.MOD_ID, Registries.PARTICLE_TYPE);

    public static final Supplier<CubeParticleType<CubeParticleOptions>> BLOCK = PARTICLE_TYPES.register("block",
            () -> new CubeParticleType<>(false));

    public static void register() {
        PARTICLE_TYPES.register();
    }

}
