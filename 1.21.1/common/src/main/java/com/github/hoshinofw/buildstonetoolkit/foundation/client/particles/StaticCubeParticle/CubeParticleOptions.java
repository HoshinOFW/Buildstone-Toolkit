package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

@DeleteMethodsAndFields({"writeToNetwork", "writeToString", "CODEC"})
public abstract class CubeParticleOptions implements ParticleOptions {
    // Read and write information, typically for use in commands
    // Since there is no information in this type, this will be an empty string
    @ShadowVersion
    public static final CubeParticleOptions INSTANCE;

    public static final MapCodec<CubeParticleOptions> CODEC = MapCodec.unit(INSTANCE);

    // Read and write information to the network buffer.
    public static final StreamCodec<ByteBuf, CubeParticleOptions> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    // Does not need any parameters, but may define any fields necessary for the particle to work.
    @ShadowVersion
    public CubeParticleOptions() {}

    @ShadowVersion
    @Override
    public abstract @NotNull ParticleType<?> getType();
}
