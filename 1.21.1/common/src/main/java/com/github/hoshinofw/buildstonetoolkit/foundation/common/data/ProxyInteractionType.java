package com.github.hoshinofw.buildstonetoolkit.foundation.common.data;

import com.github.hoshinofw.multiversion.ShadowVersion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public enum ProxyInteractionType {
    ;

    @ShadowVersion
    ProxyInteractionType(int index) {}

    @ShadowVersion
    public static native ProxyInteractionType of(int index);

    public static final StreamCodec<ByteBuf, ProxyInteractionType> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ProxyInteractionType decode(ByteBuf buf) {
            return ProxyInteractionType.of(buf.readInt());
        }

        @Override
        public void encode(ByteBuf buf, ProxyInteractionType interactionType) {
            buf.writeInt(interactionType.getIndex());
        }
    };

    public static final StreamCodec<FriendlyByteBuf, ProxyInteractionType[]> ARRAY_STREAM_CODEC = new StreamCodec<>() {

        @Override
        public void encode(@NotNull FriendlyByteBuf byteBuf, ProxyInteractionType @NotNull [] proxyInteractionTypes) {
            int[] array = new int[proxyInteractionTypes.length];
            int index = 0;
            for (ProxyInteractionType type : proxyInteractionTypes) {
                array[index] = type.getIndex();
                index++;
            }
            byteBuf.writeVarIntArray(array);
        }

        @Override
        public ProxyInteractionType @NotNull [] decode(@NotNull FriendlyByteBuf friendlyByteBuf) {
            int[] array = friendlyByteBuf.readVarIntArray();
            int index = 0;
            ProxyInteractionType[] array2 = new ProxyInteractionType[array.length];
            for (int i : array) {
                array2[index] = ProxyInteractionType.of(i);
                index++;
            }
            return array2;
        }
    };

    @ShadowVersion
    public native int getIndex();
}