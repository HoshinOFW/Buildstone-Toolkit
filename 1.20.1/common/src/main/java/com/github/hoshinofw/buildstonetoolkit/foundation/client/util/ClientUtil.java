package com.github.hoshinofw.buildstonetoolkit.foundation.client.util;

import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ClientUtil {

    private static final class ClippingRaycastContext {
        final Vec3 from;
        final Vec3 to;
        final Level level;
        final Predicate<BlockPos> test;
        @Nullable BlockHitResult clipHit = null; 

        ClippingRaycastContext(Vec3 from, Vec3 to, Level level, Predicate<BlockPos> test) {
            this.from = from;
            this.to = to;
            this.level = level;
            this.test = test;
        }
    }

    /**
     * Raycast and return the first block to pass {@code test}. If no blocks pass {@code test}, return first occluded block.
     * @param test Will be tested on each position. If it returns false, traversal continues. If it returns true, current position is returned.
     * @return If {@code test} never returns false, return first clipped block.
     */
    public static @NotNull BlockHitResult raycastWithTest(Player player, Predicate<BlockPos> test, final double ray_distance) {

        Vec3 from = player.getEyePosition(1.0F);
        Vec3 to = from.add(player.getViewVector(1.0F).scale(ray_distance));
        Level level = player.level();

        return nearestProjectedHit(level, from, to, (rayFrom, rayTo) -> {
            ClippingRaycastContext context = new ClippingRaycastContext(rayFrom, rayTo, level, test);

            return BlockGetter.traverseBlocks(rayFrom, rayTo, context, (ctx, pos) -> {
                if (ctx.test.test(pos)) return entryHit(ctx.from, ctx.to, pos);

                if (ctx.clipHit == null) {
                    BlockState state = ctx.level.getBlockState(pos);
                    VoxelShape shape = state.getShape(ctx.level, pos);
                    if (!shape.isEmpty()) {
                        BlockHitResult hit = shape.clip(ctx.from, ctx.to, pos);
                        if (hit != null) ctx.clipHit = hit.withPosition(pos.immutable());
                    }
                }
                return null;

            }, ctx -> {
                if (ctx.clipHit != null) return ctx.clipHit;
                Vec3 d = ctx.from.subtract(ctx.to);
                return BlockHitResult.miss(ctx.to, Direction.getNearest(d.x, d.y, d.z), BlockPos.containing(ctx.to));
            });
        });
    }

    /** Entry-face hit of the ray against the full cube at {@code pos}; never null for a cell the ray traverses. */
    private static BlockHitResult entryHit(Vec3 from, Vec3 to, BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        BlockHitResult hit = Shapes.block().clip(from, to, pos);
        if (hit != null) return hit.withPosition(immutablePos);
        // Corner/epsilon edge case: synthesize the dominant-axis entry face.
        Vec3 d = from.subtract(to);
        return new BlockHitResult(Vec3.atCenterOf(immutablePos), Direction.getNearest(d.x, d.y, d.z), immutablePos, true);
    }

    public static long[] raycastAndCollectBlocks(LocalPlayer player, final double ray_distance) {
        Vec3 startPos = player.getEyePosition(1.0F);
        Vec3 endPos = startPos.add(player.getViewVector(1.0F).scale(ray_distance));

        LongArrayList collected = new LongArrayList((int) Math.ceil(ray_distance * 2));
        forEachProjectedRay(player.level(), startPos, endPos, (from, to) ->
                BlockGetter.traverseBlocks(from, to, collected, (list, pos) -> {
                    list.add(pos.asLong());
                    return null;
                }, list -> null));

        return collected.toLongArray();
    }

    public static final class ClippingCollectingRaycastContext {
        final Vec3 from;
        final Vec3 to;
        final Level level;
        final LongPredicate test;
        final boolean includeExit;
        final LongArrayList collected;

        public ClippingCollectingRaycastContext(Vec3 from, Vec3 to, Level level, LongPredicate test, boolean includeExit, LongArrayList collected) {
            this.from = from;
            this.to = to;
            this.level = level;
            this.test = test;
            this.includeExit = includeExit;
            this.collected = collected;
        }
    }

    public static BooleanObjectPair<long[]> raycastAndCollectBlocksUntil(LocalPlayer player, final double ray_distance, LongPredicate test, final boolean includeExit) {
        Vec3 startPos = player.getEyePosition(1.0F);
        Vec3 endPos = startPos.add(player.getViewVector(1.0F).scale(ray_distance));

        LongArrayList collected = new LongArrayList((int) Math.ceil(ray_distance * 2));
        Level level = player.level();
        boolean[] stoppedEarly = {false};

        forEachProjectedRay(level, startPos, endPos, (from, to) -> {
            ClippingCollectingRaycastContext context = new ClippingCollectingRaycastContext(from, to, level, test, includeExit, collected);

            boolean stopped = BlockGetter.traverseBlocks(from, to, context, (ctx, pos) -> {
                long packed = pos.asLong();
                if (ctx.test.test(packed)) {
                    ctx.collected.add(packed);
                    return null;
                }
                if (includeExit) ctx.collected.add(packed);
                return Boolean.TRUE;
            }, list -> Boolean.FALSE);

            stoppedEarly[0] |= stopped;
        });

        return BooleanObjectPair.of(stoppedEarly[0], collected.toLongArray());
    }

    public static BooleanObjectPair<long[]> raycastAndCollectBlocksUntilOccluded(
            LocalPlayer player, ClientLevel level, final double ray_distance,
            BiPredicate<BlockState, BlockPos> seeThrough, final boolean includeOccluder) {
        Vec3 from = player.getEyePosition(1.0F);
        Vec3 to = from.add(player.getViewVector(1.0F).scale(ray_distance));

        LongArrayList collected = new LongArrayList((int) Math.ceil(ray_distance * 2));
        boolean[] occluded = {false};

        forEachProjectedRay(level, from, to, (rayFrom, rayTo) -> {
            Boolean rayOccluded = BlockGetter.traverseBlocks(rayFrom, rayTo, level, (lvl, pos) -> {
                BlockState state = lvl.getBlockState(pos);
                if (!seeThrough.test(state, pos)) {
                    VoxelShape shape = state.getShape(lvl, pos);
                    if (!shape.isEmpty() && shape.clip(rayFrom, rayTo, pos) != null) {
                        if (includeOccluder) collected.add(pos.asLong());
                        return Boolean.TRUE;
                    }
                }
                collected.add(pos.asLong());
                return null;
            }, lvl -> Boolean.FALSE);

            occluded[0] |= rayOccluded;
        });

        return BooleanObjectPair.of(occluded[0], collected.toLongArray());
    }
    
    private static void forEachProjectedRay(Level level, Vec3 from, Vec3 to, BiConsumer<Vec3, Vec3> body) {
        body.accept(from, to);
    }
    
    private static BlockHitResult nearestProjectedHit(Level level, Vec3 from, Vec3 to, BiFunction<Vec3, Vec3, BlockHitResult> tracer) {
        return tracer.apply(from, to);
    }

}