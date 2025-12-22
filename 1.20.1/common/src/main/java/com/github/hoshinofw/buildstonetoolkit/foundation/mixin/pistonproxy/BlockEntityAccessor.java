package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.pistonproxy;

import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PistonMovingBlockEntity.class)
public interface BlockEntityAccessor {

    @Accessor("progressO")
    float getProgressO();
}
