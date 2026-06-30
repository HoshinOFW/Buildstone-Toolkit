package com.github.hoshinofw.buildstonetoolkit.neoforge.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.platform.Platform;
import net.neoforged.fml.common.Mod;

@Mod(BuildstoneToolkit.MOD_ID)
public final class BuildstoneToolkitNeoForge {
    public BuildstoneToolkitNeoForge() {
        BuildstoneToolkit.setIsDedicatedServer(Platform.getEnv().isDedicatedServer());
        BuildstoneToolkit.init();
    }
}
