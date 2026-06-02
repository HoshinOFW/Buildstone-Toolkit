package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;

public record RenderTransformContext(Vec3 translation, Quaterniondc rotation) {
}
