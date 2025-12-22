package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PistonMovingBlockEntityMixinInterface;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Util {

    public static StreamCodec<FriendlyByteBuf, Collection<BlockPos>> POSCOLLECTION_STREAM_CODEC = new StreamCodec<FriendlyByteBuf, Collection<BlockPos>>() {
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

    public static StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = new StreamCodec<ByteBuf, Vec3>() {
        @Override
        public Vec3 decode(ByteBuf buf) {
            return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void encode(ByteBuf buf, Vec3 vec3) {
            buf.writeDouble(vec3.x);
            buf.writeDouble(vec3.y);
            buf.writeDouble(vec3.z);
        }
    };

    public static BlockHitResult raycastBlockIgnoringReach(LocalPlayer player, Level level, double maxDistance) {
        float partialTick = 1.0f;

        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 look = player.getViewVector(partialTick);
        Vec3 end = eye.add(look.scale(maxDistance));

        ClipContext ctx = new ClipContext(
                eye,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        );

        return level.clip(ctx);
    }

    public static int calculateSignal(double distance) {
        if (distance <= 2.0) return 1;
        if (distance >= 64.0) return 15;

        double t = (distance - 2.0) / (64.0 - 2.0);

        double value = 1.0 + 14.0 * (t * t);

        int signal = (int) Math.round(value);

        if (signal < 1) return 1;
        if (signal > 15) return 15;
        return signal;
    }

    public static CompoundTag getProxyTag(PistonMovingBlockEntity mbe) {
        return ((PistonMovingBlockEntityMixinInterface) mbe).buildstonetoolkit$getProxyTag();
    }

    public static Component blueComponent(String string) {
        return Component.literal(string).withStyle(ChatFormatting.BLUE);
    }

    public static Component yellowComponent(String string) {
        return Component.literal(string).withStyle(ChatFormatting.YELLOW);
    }

    public static boolean deepEquals(@Nullable BlockPos a, @Nullable BlockPos b) {
        if ((a==null && b==null)) return true;
        if (a==null ^ b==null) return false;
        return ((a.getX() == b.getX()) && (a.getY() == b.getY()) && (a.getZ() == b.getZ()));
    }

    public static boolean isPushableBlockEntity(BlockState state) {
        return (state.hasBlockEntity() && (state.getBlock() instanceof PistonProxyBlock));
    }

    public static Direction resolveDirection(Direction direction, boolean isExtending) {
        return isExtending ? direction : direction.getOpposite();
    }

    public static int[] blockPosToArray(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static int max(int a, int b, int c) {
        return (Math.max(a, Math.max(b, c)));
    }

    public static boolean isNonZeroOnlyOn(BlockPos pos, Direction.Axis axis) {
        return switch (axis) {
            case X -> pos.getX() != 0 && pos.getY() == 0 && pos.getZ() == 0;
            case Y -> pos.getY() != 0 && pos.getX() == 0 && pos.getZ() == 0;
            case Z -> pos.getZ() != 0 && pos.getX() == 0 && pos.getY() == 0;
        };
    }

    public static BlockPos projectOntoAxis(BlockPos rawPos, Direction.Axis axis) {
        return switch (axis) {
            case X -> new BlockPos(rawPos.getX(), 0, 0);
            case Y -> new BlockPos(0, rawPos.getY(), 0);
            case Z -> new BlockPos(0, 0, rawPos.getZ());
        };
    }

    public static boolean isZero(BlockPos pos){
        return pos.asLong() == 0;
    }

    public static Direction getPositiveDirection(Direction.Axis axis) {
        Direction positiveDirection;
        switch (axis) {
            case X -> positiveDirection = Direction.EAST;
            case Z -> positiveDirection = Direction.SOUTH;
            default -> positiveDirection = Direction.UP;
        }

        return positiveDirection;
    }

    public static Set<BlockPos> convertToBlockPos(Collection<Long> longCollection) {
        Set<BlockPos> posSet = new HashSet<>();
        for (Long pos : longCollection) {
            posSet.add(BlockPos.of(pos));
        }
        return posSet;
    }

    public static Set<Long> convertToLongPos(Collection<BlockPos> posCollection) {
        Set<Long> longSet = new HashSet<>();
        for (BlockPos pos : posCollection) {
            longSet.add(pos.asLong());
        }
        return longSet;
    }

    public record FailableResult<T>(T value, boolean succeeded) {
        public static <T> FailableResult<T> success(T value) {
            return new FailableResult<>(value, true);
        }
        public static <T> FailableResult<T> failure(T value) {
            return new FailableResult<>(value, false);
        }
    }

    public static class ValueCycler<T> {
        private final List<T> values;
        private final java.util.function.Consumer<T> setter;
        private final java.util.function.Supplier<T> getter;

        public ValueCycler(List<T> values, Supplier<T> getter, Consumer<T> setter) {
            this.values = values;
            this.getter = getter;
            this.setter = setter;
        }

        public void next() {
            T current = getter.get();
            int index = values.indexOf(current);
            if (index == -1) index = 0;
            int nextIndex = (index + 1) % values.size();
            setter.accept(values.get(nextIndex));
        }
    }
}
