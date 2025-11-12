package com.github.hoshinofw.buildstonetoolkit.fabric.core;

import net.fabricmc.api.ModInitializer;

import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;

public final class BuildstoneToolkitFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        BuildstoneToolkit.init();

    }
}
