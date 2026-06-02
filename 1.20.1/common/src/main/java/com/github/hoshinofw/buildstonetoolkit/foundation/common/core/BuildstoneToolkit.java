package com.github.hoshinofw.buildstonetoolkit.foundation.common.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.create.CreateCompatInit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.config.BTConfig;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.events.BuildstoneCommonEvents;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.*;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuildstoneToolkit {
    public static final String MOD_ID = "buildstonetoolkit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean IS_SABLE_LOADED = false;

    public static ResourceLocation RLFromPath(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void init() {
        LOGGER.info("Debug enabled: {}", LOGGER.isDebugEnabled());
        BuildstoneBlocks.register();
        BuildstoneBlockEntities.register();
        BuildstoneItems.register();
        BuildstoneParticles.register();
        //Events
        BuildstoneCommonEvents.register();
        BuildstonePackets.register();

        logVersion();
        LOGGER.info("Finished common-init");
    }

    //After registries are flushed
    public static void postInit() {
        BTConfig.init();
        if (Platform.isModLoaded("create")) CreateCompatInit.init();

        if (Platform.isModLoaded("sable")) {
            IS_SABLE_LOADED = true;
            sableCompatPostInit();
        }

        if (Platform.isModLoaded("simulated")) simulatedCompatPostInit();

        LOGGER.info("Finished post-common-init");
    }

    private static void logVersion() {
        LOGGER.info("Common version 1.20.1");
    }

    public static void onServerLoad(ServerLevel serverLevel) {
        BTConfig.init();
    }

    private static void sableCompatPostInit() {

    }

    private static void simulatedCompatPostInit() {

    }
}
