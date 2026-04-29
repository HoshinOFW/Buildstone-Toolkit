package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class CodecUtil {

    public static StreamCodec<FriendlyByteBuf, Collection<BlockPos>> POSCOLLECTION_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Collection<BlockPos> decode(FriendlyByteBuf buf) {
            ArrayList<BlockPos> array = new ArrayList<>();
            for (long i : buf.readLongArray()) {
                array.add(BlockPos.of(i));
            }
            return array;
        }

        @Override
        public void encode(FriendlyByteBuf buf, Collection<BlockPos> collection) {
            long[] array = new long[collection.size()];
            int index = 0;
            for (BlockPos pos : collection) {
                array[index] = pos.asLong();
                index++;
            }
            buf.writeLongArray(array);
        }
    };

    public static StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Vec3 decode(ByteBuf buf) {
            return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void encode(ByteBuf buf, Vec3 vec3) {
            buf.writeDouble(vec3.x);
            buf.writeDouble(vec3.y);
            buf.writeDouble(vec3.z);
        }
    };
}
