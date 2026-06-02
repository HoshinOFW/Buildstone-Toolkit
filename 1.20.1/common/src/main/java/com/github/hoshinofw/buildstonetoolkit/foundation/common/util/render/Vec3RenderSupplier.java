package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Vec3RenderSupplier {
    @Nullable
    Vec3 getVec3(float partialTicks);

}
