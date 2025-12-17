package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PistonMovingBlockEntityMixinInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Util {
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
        private final Consumer<T> setter;
        private final Supplier<T> getter;

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
