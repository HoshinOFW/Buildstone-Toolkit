package com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle;

import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneParticles;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class BlockParticleOptions implements ParticleOptions {
    // Read and write information, typically for use in commands
    // Since there is no information in this type, this will be an empty string

    public static final BlockParticleOptions INSTANCE = new BlockParticleOptions();

    public static final MapCodec<BlockParticleOptions> CODEC = MapCodec.unit(INSTANCE);

    // Read and write information to the network buffer.
    public static final StreamCodec<ByteBuf, BlockParticleOptions> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    // Does not need any parameters, but may define any fields necessary for the particle to work.
    public BlockParticleOptions() {}

    @Override
    public @NotNull ParticleType<?> getType() {
        return BuildstoneParticles.BLOCK.get();
    }
}
