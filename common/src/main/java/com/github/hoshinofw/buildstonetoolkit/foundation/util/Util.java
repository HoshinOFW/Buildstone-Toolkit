package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.PistonProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PistonMovingBlockEntityMixinInterface;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PlayerMixinInterface;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.IdObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Util {

    public static final String NBTIdKey = "buildstonetoolkit$id";
    public static final String NBTTargetPosKey = "buildstonetoolkit$target";
    public static final String OLDNBTTargetPosKey = "relativeTargetPos";

    public static int max(int a, int b, int c) {
        return (Math.max(a, Math.max(b, c)));
    }

    public static Long getTargetPosFromNBT(CompoundTag nbt) {
        //Handle tag missing
        if (!nbt.contains(NBTTargetPosKey, Tag.TAG_LONG)) {
            //Try defaulting to the old NBT
            long oldTagPos = OLDgetTargetPosFromNBT(nbt).asLong();
            if (oldTagPos != 0) {
                return oldTagPos;
            }

            //Try defaulting to the block's position
            if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z")) {
                return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")).asLong();
            }
            //Fallback to zero.
            return 0L;
        } else {
            //Normal behavior
            return nbt.getLong(NBTTargetPosKey);
        }
    }

    public static void savePosToNBT(CompoundTag nbt, BlockPos pos) {
        savePosToNBT(nbt, pos.asLong());
    }

    public static void savePosToNBT(CompoundTag nbt, long pos) {
        nbt.putLong(NBTTargetPosKey, pos);
    }

    public static CompoundTag getTargetNBTFromProxy(ProxyBlockEntity proxy) {
        CompoundTag nbt = new CompoundTag();
        savePosToNBT(nbt, proxy.getLinkedAbsLongPos());
        return nbt;
    }

    public static void saveTargetNBTFromProxy(CompoundTag nbt, ProxyBlockEntity proxy) {
        nbt.merge(getTargetNBTFromProxy(proxy));
    }

    @Deprecated
    public static BlockPos OLDgetTargetPosFromNBT(CompoundTag nbt) {
        int[] array = nbt.getIntArray(OLDNBTTargetPosKey);

        if (!nbt.contains(OLDNBTTargetPosKey, Tag.TAG_INT_ARRAY) || (array.length < 3)) {return BlockPos.ZERO;}

        return new BlockPos(array[0], array[1], array[2]);
    }


    public static CompoundTag getProxyTag(PistonMovingBlockEntity mbe) {
        return ((PistonMovingBlockEntityMixinInterface) mbe).buildstonetoolkit$getProxyTag();
    }

    public static BlockPos getSelectedPos(@NotNull Player player) {
        return ((PlayerMixinInterface)player).buildstonetoolkit$getSelectedBlockPos();
    }

    public static void setSelectedPos(@NotNull Player player, @Nullable BlockPos newPos) {
        ((PlayerMixinInterface)player).buildstonetoolkit$setSelectedBlockPos(newPos);
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

    public static void saveId(CompoundTag nbt, IdObject object) {
        nbt.putLong(NBTIdKey, object.getId());
    }

    public static long getId(CompoundTag nbt) {
        if (nbt.contains(NBTIdKey, Tag.TAG_LONG)) {
            return nbt.getLong(NBTIdKey);
        } else {
            return -1L;
        }
    }

    public static @Nullable Vec3 getSearchOriginFromNBT(CompoundTag nbt) {

        if (nbt.contains("searchOrigin", Tag.TAG_INT_ARRAY)) {
            int[] array = nbt.getIntArray("searchOrigin");
            return new BlockPos(array[0], array[1], array[2]).getCenter();
        }
        else {return null;
        }
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
