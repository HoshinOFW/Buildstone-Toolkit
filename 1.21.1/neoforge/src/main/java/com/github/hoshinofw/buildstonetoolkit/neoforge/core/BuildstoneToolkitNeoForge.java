package com.github.hoshinofw.buildstonetoolkit.neoforge.core;

import net.neoforged.fml.common.Mod;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;

@Mod(BuildstoneToolkit.MOD_ID)
public final class BuildstoneToolkitNeoForge {
    public BuildstoneToolkitNeoForge() {
        // Run our common setup.
        BuildstoneToolkit.init();

    }
}
