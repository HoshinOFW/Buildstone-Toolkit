package com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle;

import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class BlockParticleOptions implements ParticleOptions {
    // Read and write information, typically for use in commands
    // Since there is no information in this type, this will be an empty string

    public static final BlockParticleOptions INSTANCE = new BlockParticleOptions();

    public static final Codec<BlockParticleOptions> CODEC = Codec.unit(INSTANCE);

    // Does not need any parameters, but may define any fields necessary for the particle to work.
    public BlockParticleOptions() {}

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
