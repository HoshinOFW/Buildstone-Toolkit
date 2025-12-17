package com.github.hoshinofw.buildstonetoolkit.foundation.common.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneParticles;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events.BuildstoneCommonEvents;
import com.github.hoshinofw.buildstonetoolkit.registries.*;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuildstoneToolkit {
    public static final String MOD_ID = "buildstonetoolkit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        MixinExtrasBootstrap.init();

        BuildstoneBlocks.register();
        BuildstoneBlockEntities.register();
        BuildstoneItems.register();
        BuildstoneCommonEvents.register();
        BuildstoneParticles.register();
        //Events
        BuildstoneCommonEvents.register();
        BuildstonePackets.register();

        LOGGER.info("Finished registries");
    }
}
