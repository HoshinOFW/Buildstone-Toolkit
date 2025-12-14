package com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin;

public class NeighborUpdaterMixinHelper {
    public static final ThreadLocal<Boolean> skipProxy = ThreadLocal.withInitial(() -> false);
}
