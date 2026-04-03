package com.github.hoshinofw.buildstonetoolkit.fabric.core;

import com.github.hoshinofw.buildstonetoolkit.fabric.registries.BuildstoneFabricClientParticles;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.core.BuildstoneToolkitClient;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import net.fabricmc.api.ClientModInitializer;

public final class BuildstoneToolkitFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BuildstoneToolkitClient.init();

        BuildstoneFabricClientParticles.register();;
        BuildstoneToolkit.LOGGER.info("Finished Fabric client init");

    }
}
