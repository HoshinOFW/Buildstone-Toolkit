package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class CubeParticleOptions implements ParticleOptions {

    public static final CubeParticleOptions INSTANCE = new CubeParticleOptions();

    public static final Codec<CubeParticleOptions> CODEC = Codec.unit(INSTANCE);

    public CubeParticleOptions() {}

    @Override
    public @NotNull ParticleType<?> getType() {
        return BuildstoneParticles.BLOCK.get();
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public @NotNull String writeToString() {
        return "ParticleOptions: BlockParticleOptions";
    }
}
