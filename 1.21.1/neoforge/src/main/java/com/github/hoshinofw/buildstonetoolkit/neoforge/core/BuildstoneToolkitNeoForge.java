package com.github.hoshinofw.buildstonetoolkit.neoforge.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.platform.Platform;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.targets.NeoForgeServerDevLaunchHandler;
import net.neoforged.fml.loading.targets.NeoForgeServerLaunchHandler;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BuildstoneToolkit.MOD_ID)
public final class BuildstoneToolkitNeoForge {
    public BuildstoneToolkitNeoForge() {
        BuildstoneToolkit.setIsDedicatedServer(Platform.getEnv().isDedicatedServer());
        BuildstoneToolkit.init();
    }
}
