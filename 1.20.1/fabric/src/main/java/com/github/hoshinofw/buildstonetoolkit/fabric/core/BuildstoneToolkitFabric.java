package com.github.hoshinofw.buildstonetoolkit.fabric.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import net.fabricmc.api.ModInitializer;


public final class BuildstoneToolkitFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BuildstoneToolkit.init();

    }
}
