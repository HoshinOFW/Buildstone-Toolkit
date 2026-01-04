package com.github.hoshinofw.buildstonetoolkit.forge.core;

import com.github.hoshinofw.buildstonetoolkit.forge.registries.BuildstoneForgeClientEvents;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.core.BuildstoneToolkitClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.github.hoshinofw.buildstonetoolkit.forge.core.BuildstoneToolkitForge.MOD_BUS;

public class BuildstoneToolkitForgeClient {

    public static void init() {
        MOD_BUS.addListener(BuildstoneToolkitForgeClient::onClientSetup);
        MOD_BUS.addListener(BuildstoneForgeClientEvents::registerParticleProviders);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        BuildstoneToolkitClient.init();
    }

}
