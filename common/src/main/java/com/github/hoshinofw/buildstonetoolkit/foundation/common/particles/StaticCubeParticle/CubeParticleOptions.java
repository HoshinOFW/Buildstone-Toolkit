package com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class CubeParticleOptions implements ParticleOptions {
    // Read and write information, typically for use in commands
    // Since there is no information in this type, this will be an empty string

    public static final CubeParticleOptions INSTANCE = new CubeParticleOptions();

    public static final MapCodec<CubeParticleOptions> CODEC = MapCodec.unit(INSTANCE);

    // Read and write information to the network buffer.
    public static final StreamCodec<ByteBuf, CubeParticleOptions> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    // Does not need any parameters, but may define any fields necessary for the particle to work.
    public CubeParticleOptions() {}

    @Override
    public @NotNull ParticleType<?> getType() {
        return BuildstoneParticles.BLOCK.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) { //IMPLEMENT
    }

    @Override
    public @NotNull String writeToString() {
        return "ParticleOptions: BlockParticleOptions";
    }
}
