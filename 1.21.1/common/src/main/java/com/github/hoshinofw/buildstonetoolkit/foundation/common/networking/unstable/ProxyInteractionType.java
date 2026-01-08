package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public enum ProxyInteractionType {

    RightClickedRightClickProxy(1),
    StoppedRightClickingRightClockProxy(2),
    LookedAtLookingAtProxy(3),
    StoppedLookingAtLookingAtProxy(4);

    private final int index;
    ProxyInteractionType(int index) {
        this.index = index;
    }

    private static final ProxyInteractionType[] BY_INDEX;

    static {
        int max = 0;
        for (ProxyInteractionType type : values()) {
            max = Math.max(max, type.index);
        }

        BY_INDEX = new ProxyInteractionType[max + 1];
        for (ProxyInteractionType type : values()) {
            BY_INDEX[type.index] = type;
        }
    }

    public static ProxyInteractionType of(int index) {
        if (index < 0 || index >= BY_INDEX.length || BY_INDEX[index] == null) {
            throw new IllegalArgumentException(
                    "Unknown ProxyInteractionType index: " + index
            );
        }
        return BY_INDEX[index];
    }

    public static final StreamCodec<ByteBuf, ProxyInteractionType> STREAM_CODEC = new StreamCodec<ByteBuf, ProxyInteractionType>() {
        @Override
        public @NotNull ProxyInteractionType decode(ByteBuf buf) {
            return ProxyInteractionType.of(buf.readInt());
        }

        @Override
        public void encode(ByteBuf buf, ProxyInteractionType interactionType) {
            buf.writeInt(interactionType.getIndex());
        }
    };

    public int getIndex() {
        return this.index;
    }
}