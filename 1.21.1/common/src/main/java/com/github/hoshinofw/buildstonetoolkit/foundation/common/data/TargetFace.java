package com.github.hoshinofw.buildstonetoolkit.foundation.common.data;

import com.github.hoshinofw.multiversion.ShadowVersion;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TargetFace implements StringRepresentable {
    @ShadowVersion
    ALL
    ;

    @ShadowVersion
    private final int index;

    public static final StreamCodec<ByteBuf, TargetFace> STREAM_CODEC =  new StreamCodec<ByteBuf, TargetFace>() {
        @Override
        public TargetFace decode(ByteBuf byteBuf) {
            return TargetFace.fromIndex(byteBuf.readInt());
        }

        @Override
        public void encode(ByteBuf buf, TargetFace targetFace) {
            buf.writeInt(targetFace.index);
        }
    };

    @Override
    @ShadowVersion
    public @NotNull String getSerializedName();

    @ShadowVersion
    public static TargetFace fromDirection(Direction direction);

    @ShadowVersion
    public static TargetFace fromIndex(int index);


}
