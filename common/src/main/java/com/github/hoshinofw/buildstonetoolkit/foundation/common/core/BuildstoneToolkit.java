package com.github.hoshinofw.buildstonetoolkit.foundation.common.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlocks;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events.BuildstoneCommonEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuildstoneToolkit {
    public static final String MOD_ID = "buildstonetoolkit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        BuildstoneBlocks.register();
        BuildstoneBlockEntities.register();
        BuildstoneItems.register();
        BuildstoneParticles.register();
        //Events
        BuildstoneCommonEvents.register();

        LOGGER.info("Finished registries");
    }
}
