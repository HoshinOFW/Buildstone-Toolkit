package com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class BlockParticleType<T extends BlockParticleOptions> extends ParticleType<BlockParticleOptions> {

    public BlockParticleType(boolean bl) {
        super(bl);
    }

    @Override
    public @NotNull MapCodec<BlockParticleOptions> codec() {
        return BlockParticleOptions.CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOptions> streamCodec() {
        return BlockParticleOptions.STREAM_CODEC;
    }
}
