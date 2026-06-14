package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render;

@FunctionalInterface
public interface RenderTransformSupplier {

    RenderTransformContext get(float partialTicks);

    default void tick() {}

}
