package com.github.hoshinofw.buildstonetoolkit.foundation.common.core;


import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events.BuildstoneCommonEvents;
import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ModifyClass
public final class BuildstoneToolkit {

    @ShadowVersion
    public static final Logger LOGGER;

    @ShadowVersion
    public static final String MOD_ID;

    @OverwriteVersion
    public static void logVersion() {
        LOGGER.info("Common version 1.21.1");
    }
}
