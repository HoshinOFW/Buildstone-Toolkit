package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class CubeParticleType<T extends CubeParticleOptions> extends ParticleType<CubeParticleOptions> {

    @OverwriteVersion
    public CubeParticleType(boolean bl) {
        super(bl);
    }

    @OverwriteVersion
    @Override
    public @NotNull MapCodec<CubeParticleOptions> codec() {
        return CubeParticleOptions.CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, CubeParticleOptions> streamCodec() {
        return CubeParticleOptions.STREAM_CODEC;
    }
}
