package com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class BlockParticleType<T extends BlockParticleOptions> extends ParticleType<BlockParticleOptions> {

    public BlockParticleType(boolean bl) {
        super(bl, new ParticleOptions.Deserializer<BlockParticleOptions>() {
            @Override
            public @NotNull BlockParticleOptions fromCommand(ParticleType<BlockParticleOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
                return BlockParticleOptions.INSTANCE;
            }
            @Override
            public @NotNull BlockParticleOptions fromNetwork(ParticleType<BlockParticleOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
                return BlockParticleOptions.INSTANCE;
            }
        });
    }

    @Override
    public @NotNull Codec<BlockParticleOptions> codec() {
        return BlockParticleOptions.CODEC;
    }
}
