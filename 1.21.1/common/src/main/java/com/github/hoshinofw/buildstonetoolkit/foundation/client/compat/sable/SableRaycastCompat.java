package com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SableRaycastCompat {
    //TODO Do a better job of not switching between Vec3 and Vector3d much
    public static void forEachProjectedRay(Level level, Vec3 from, Vec3 to, BiConsumer<Vec3, Vec3> body) {
        ActiveSableCompanion helper = Sable.HELPER;

        Vec3 mainFrom = projectOutward(level, helper, from);
        Vec3 mainTo = projectOutward(level, helper, to);

        Vector3d mainFromJOML = JOMLConversion.toJOML(mainFrom);
        Vector3d mainToJOML = JOMLConversion.toJOML(mainTo);

        body.accept(mainFrom, mainTo);

        BoundingBox3d bounds = new BoundingBox3d(mainFrom, mainTo);
        for (SubLevel subLevel : helper.getAllIntersecting(level, bounds)) {
            Pose3dc pose = poseOf(level, subLevel);
            Vector3dc subFrom = pose.transformPositionInverse(mainFromJOML);
            Vector3dc subTo = pose.transformPositionInverse(mainToJOML);

            if (helper.getContaining(level, subFrom) != subLevel) continue;

            body.accept(JOMLConversion.toMojang(subFrom), JOMLConversion.toMojang(subTo));
        }
    }

    public static BlockHitResult nearestProjectedHit(Level level, Vec3 from, Vec3 to, BiFunction<Vec3, Vec3, BlockHitResult> tracer) {
        ActiveSableCompanion helper = Sable.HELPER;

        Vec3 mainFrom = projectOutward(level, helper, from);
        Vec3 mainTo = projectOutward(level, helper, to);

        Vector3d mainFromJOML = JOMLConversion.toJOML(mainFrom);
        Vector3d mainToJOML = JOMLConversion.toJOML(mainTo);

        BlockHitResult best = tracer.apply(mainFrom, mainTo);
        double bestDistance = best.getType() == HitResult.Type.MISS
                ? Double.MAX_VALUE
                : best.getLocation().distanceTo(mainFrom);

        BoundingBox3d bounds = new BoundingBox3d(mainFrom, mainTo);
        for (SubLevel subLevel : helper.getAllIntersecting(level, bounds)) {
            Pose3dc pose = poseOf(level, subLevel);
            Vector3dc subFromV = pose.transformPositionInverse(mainFromJOML);
            Vector3dc subToV = pose.transformPositionInverse(mainToJOML);

            if (helper.getContaining(level, subFromV) != subLevel) continue;

            Vec3 subFrom = JOMLConversion.toMojang(subFromV);
            BlockHitResult subResult = tracer.apply(subFrom, JOMLConversion.toMojang(subToV));
            if (subResult.getType() == HitResult.Type.MISS) continue;

            double distance = subResult.getLocation().distanceTo(subFrom);
            if (best.getType() == HitResult.Type.MISS || distance < bestDistance) {
                best = subResult;
                bestDistance = distance;
            }
        }

        return best;
    }

    private static Vec3 projectOutward(Level level, ActiveSableCompanion helper, Vec3 point) {
        return helper.projectOutOfSubLevel(level, point);
    }

    private static Pose3dc poseOf(Level level, SubLevel subLevel) {
        if (level instanceof LevelPoseProviderExtension extension) {
            return extension.sable$getPose(subLevel);
        }
        return subLevel.logicalPose();
    }
}