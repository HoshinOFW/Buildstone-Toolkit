package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PistonMovingBlockEntityMixinInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class Util {

    public static boolean isWithin(BlockPos pos, BlockPos cornerA, BlockPos cornerB) {
        int minX = Math.min(cornerA.getX(), cornerB.getX());
        int minY = Math.min(cornerA.getY(), cornerB.getY());
        int minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        int maxX = Math.max(cornerA.getX(), cornerB.getX());
        int maxY = Math.max(cornerA.getY(), cornerB.getY());
        int maxZ = Math.max(cornerA.getZ(), cornerB.getZ());
        return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getY() >= minY && pos.getY() <= maxY
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    public static boolean isVirtualRenderWorld(Level level) {
        if (level == null) return false;
        return level.getClass().getName().equals("com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld");
    }

    public static class PositionImpl implements Position {
        private final Vec3 vec3;

        public PositionImpl(Vec3 vec3) {
            this.vec3 = vec3;
        }

        @Override
        public double x() {
            return vec3.x();
        }

        @Override
        public double y() {
            return vec3.y();
        }

        @Override
        public double z() {
            return vec3.z();
        }
    }

    public static CompoundTag getProxyTag(PistonMovingBlockEntity mbe) {
        return ((PistonMovingBlockEntityMixinInterface) mbe).buildstonetoolkit$getProxyTag();
    }

    public static Component blueComponent(String string) {
        return Component.literal(string).withStyle(ChatFormatting.BLUE);
    }

    public static Direction resolveDirection(Direction direction, boolean isExtending) {
        return isExtending ? direction : direction.getOpposite();
    }

    /**
     *
     * @param packedPos long-packed vanilla minecraft BlockPos. Other encodings will probably not work.
     * @param direction exactly what you think it is
     * @return
     */
    public static long fastRelative(long packedPos, Direction direction) {
        return BlockPos.asLong(
                BlockPos.getX(packedPos) + direction.getStepX(),
                BlockPos.getY(packedPos) + direction.getStepY(),
                BlockPos.getZ(packedPos) + direction.getStepZ());
    }

    
    public static long fastOffset(long packedPos, long packedOffset) {
        return BlockPos.asLong(
                BlockPos.getX(packedPos) + BlockPos.getX(packedOffset),
                BlockPos.getY(packedPos) + BlockPos.getY(packedOffset),
                BlockPos.getZ(packedPos) + BlockPos.getZ(packedOffset));
    }

    public static int[] blockPosToArray(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static Set<BlockPos> convertToBlockPos(Collection<Long> longCollection) {
        Set<BlockPos> posSet = new HashSet<>();
        for (Long pos : longCollection) {
            posSet.add(BlockPos.of(pos));
        }
        return posSet;
    }

}
