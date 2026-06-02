package com.github.hoshinofw.buildstonetoolkit.foundation.common.core;


import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable.SableCompatInit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.simulated.SimulatedCompatInit;
import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

@ModifyClass
public final class BuildstoneToolkit {

    @OverwriteVersion
    public static ResourceLocation RLFromPath(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @ShadowVersion
    public static boolean IS_SABLE_LOADED;

    @ShadowVersion
    public static final Logger LOGGER;

    @ShadowVersion
    public static final String MOD_ID;

    @OverwriteVersion
    private static void logVersion() {
        LOGGER.info("Common version 1.21.1");
    }

    @OverwriteVersion
    private static void sableCompatPostInit() {
        SableCompatInit.init();
    }

    @OverwriteVersion
    private static void simulatedCompatPostInit() {
        SimulatedCompatInit.init();
    }
}
