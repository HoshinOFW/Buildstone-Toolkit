package com.github.hoshinofw.buildstonetoolkit.neoforge.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import net.neoforged.fml.common.Mod;

@Mod(BuildstoneToolkit.MOD_ID)
public final class BuildstoneToolkitNeoForge {
    public BuildstoneToolkitNeoForge() {
        // Run our common setup.
        BuildstoneToolkit.init();

    }
}
