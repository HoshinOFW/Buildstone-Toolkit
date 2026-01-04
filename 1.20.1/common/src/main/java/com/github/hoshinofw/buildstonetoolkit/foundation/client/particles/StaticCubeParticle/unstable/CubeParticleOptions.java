package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class CubeParticleOptions implements ParticleOptions {
    // Read and write information, typically for use in commands
    // Since there is no information in this type, this will be an empty string

    public static final CubeParticleOptions INSTANCE = new CubeParticleOptions();

    public static final Codec<CubeParticleOptions> CODEC = Codec.unit(INSTANCE);

    public CubeParticleOptions() {}

    @Override
    public @NotNull ParticleType<?> getType() {
        return BuildstoneParticles.BLOCK.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public @NotNull String writeToString() {
        return "ParticleOptions: BlockParticleOptions";
    }
}
