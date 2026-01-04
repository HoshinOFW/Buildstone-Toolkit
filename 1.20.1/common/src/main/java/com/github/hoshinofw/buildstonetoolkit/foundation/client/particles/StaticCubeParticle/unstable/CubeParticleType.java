package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.unstable;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class CubeParticleType<T extends CubeParticleOptions> extends ParticleType<CubeParticleOptions> {

    public CubeParticleType(boolean bl) {
        super(bl, new ParticleOptions.Deserializer<>() {
            @Override
            public @NotNull CubeParticleOptions fromCommand(ParticleType<CubeParticleOptions> particleType, StringReader stringReader) throws CommandSyntaxException {
                return CubeParticleOptions.INSTANCE;
            }
            @Override
            public @NotNull CubeParticleOptions fromNetwork(ParticleType<CubeParticleOptions> particleType, FriendlyByteBuf friendlyByteBuf) {
                return CubeParticleOptions.INSTANCE;
            }
        });
    }

    @Override
    public @NotNull Codec<CubeParticleOptions> codec() {
        return CubeParticleOptions.CODEC;
    }
}
