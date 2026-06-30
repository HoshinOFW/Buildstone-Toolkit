package com.github.hoshinofw.buildstonetoolkit.fabric.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

public final class BuildstoneToolkitFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BuildstoneToolkit.setIsDedicatedServer(Platform.getEnvironment().toPlatform() == EnvType.SERVER);
        BuildstoneToolkit.init();
    }
}
