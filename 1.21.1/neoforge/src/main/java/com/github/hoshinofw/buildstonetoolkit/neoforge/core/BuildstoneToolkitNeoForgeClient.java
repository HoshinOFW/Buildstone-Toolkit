package com.github.hoshinofw.buildstonetoolkit.neoforge.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkitClient;
import com.github.hoshinofw.buildstonetoolkit.neoforge.registries.BuildstoneNeoForgeClientEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

@Mod(value = BuildstoneToolkit.MOD_ID, dist = Dist.CLIENT)
public class BuildstoneToolkitNeoForgeClient {

    public BuildstoneToolkitNeoForgeClient() {
        IEventBus modbus = Objects.requireNonNull(ModLoadingContext.get().getActiveContainer().getEventBus());

        modbus.register(BuildstoneNeoForgeClientEvents.class);
        modbus.addListener(BuildstoneToolkitNeoForgeClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        BuildstoneToolkitClient.init();
    }
}
