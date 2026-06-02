package com.github.hoshinofw.buildstonetoolkit.foundation.client.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable.SableRaycastCompat;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@ModifyClass
public abstract class ClientUtil {

    @OverwriteVersion
    private static void forEachProjectedRay(Level level, Vec3 from, Vec3 to, BiConsumer<Vec3, Vec3> body) {
        if (BuildstoneToolkit.IS_SABLE_LOADED) {
            SableRaycastCompat.forEachProjectedRay(level, from, to, body);
        } else {
            body.accept(from, to);
        }
    }

    @OverwriteVersion
    private static BlockHitResult nearestProjectedHit(Level level, Vec3 from, Vec3 to, BiFunction<Vec3, Vec3, BlockHitResult> tracer) {
        if (BuildstoneToolkit.IS_SABLE_LOADED) {
            return SableRaycastCompat.nearestProjectedHit(level, from, to, tracer);
        }
        return tracer.apply(from, to);
    }
}