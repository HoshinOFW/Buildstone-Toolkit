package com.github.hoshinofw.buildstonetoolkit.fabric.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.platform.Platform;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.MinecraftServerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.server.FabricServerLauncher;
import net.minecraft.server.MinecraftServer;

public final class BuildstoneToolkitFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BuildstoneToolkit.setIsDedicatedServer(Platform.getEnvironment().toPlatform() == EnvType.SERVER);
        BuildstoneToolkit.init();
    }
}
