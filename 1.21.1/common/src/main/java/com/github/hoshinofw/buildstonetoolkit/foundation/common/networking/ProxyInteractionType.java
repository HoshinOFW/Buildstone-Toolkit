package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.multiversion.ShadowVersion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public enum ProxyInteractionType {
    //Forced to have at least one here...
    @ShadowVersion RightClickedRightClickProxy(1);

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

    @ShadowVersion
    public native int getIndex();
}