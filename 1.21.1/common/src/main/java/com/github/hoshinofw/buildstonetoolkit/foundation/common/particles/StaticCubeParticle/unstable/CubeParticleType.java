package com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.unstable;

import com.github.hoshinofw.multiversion.Overwrite;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
@Overwrite
public class CubeParticleType<T extends com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.unstable.CubeParticleOptions> extends ParticleType<com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.unstable.CubeParticleOptions> {

    public CubeParticleType(boolean bl) {
        super(bl);
    }

    @Override
    public @NotNull MapCodec<com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.unstable.CubeParticleOptions> codec() {
        return CubeParticleOptions.CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, CubeParticleOptions> streamCodec() {
        return CubeParticleOptions.STREAM_CODEC;
    }
}
