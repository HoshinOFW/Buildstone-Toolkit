package com.github.hoshinofw.buildstonetoolkit.util.mixin;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface AllayMixinInterface {

    public @Nullable Vec3 buildstonetoolkit$getSearchOrigin();
    public void buildstonetoolkit$setSearchOrigin(Vec3 value);

    public double buildstonetoolkit$getMaxDistanceToOrigin();
    public void buildstonetoolkit$setMaxDistanceToOrigin(double value);

}
