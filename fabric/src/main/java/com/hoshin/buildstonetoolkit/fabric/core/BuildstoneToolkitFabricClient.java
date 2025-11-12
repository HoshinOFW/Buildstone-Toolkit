package com.hoshin.buildstonetoolkit.fabric.core;

import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkitClient;
import com.hoshin.buildstonetoolkit.fabric.registries.BuildstoneFabricClientParticles;
import net.fabricmc.api.ClientModInitializer;

public final class BuildstoneToolkitFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BuildstoneToolkitClient.init();

        BuildstoneFabricClientParticles.register();;
        BuildstoneToolkit.LOGGER.info("Finished Fabric client init");

    }
}
