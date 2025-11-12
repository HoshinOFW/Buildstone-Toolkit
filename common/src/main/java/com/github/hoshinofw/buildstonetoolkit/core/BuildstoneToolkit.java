package com.github.hoshinofw.buildstonetoolkit.core;

import com.github.hoshinofw.buildstonetoolkit.registries.*;

import com.github.hoshinofw.buildstonetoolkit.registries.events.BuildstoneCommonEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuildstoneToolkit {
    public static final String MOD_ID = "buildstonetoolkit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        BuildstoneItems.register();
        BuildstoneBlocks.register();
        BuildstoneBlockEntities.register();
        BuildstoneBlockItems.register();
        BuildstoneCommonEvents.register();
        BuildstoneParticles.register();

        LOGGER.info("Finished registries");
    }
}
