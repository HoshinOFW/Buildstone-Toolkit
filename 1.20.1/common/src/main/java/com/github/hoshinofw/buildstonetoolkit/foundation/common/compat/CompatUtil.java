package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat;

import com.github.hoshinofw.multiversion.OverwriteVersion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

public class CompatUtil {

    private static BiFunction<Level, Vec3, Vec3> ACTIVE_PROJECTION =(level, pos) -> pos;
    public static void setActiveProjection(BiFunction<Level, Vec3, Vec3> function) {
        ACTIVE_PROJECTION = function;
    }

    public static Vec3 projectOutOfSublevel(Level level, Vec3 pos) {
        return ACTIVE_PROJECTION.apply(level, pos);
    }

}
