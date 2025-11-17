package com.github.hoshinofw.buildstonetoolkit.forge.core;

import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkitClient;
import com.github.hoshinofw.buildstonetoolkit.forge.registries.BuildstoneForgeClientEvents;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Objects;

@Mod(BuildstoneToolkit.MOD_ID)
public final class BuildstoneToolkitForge {
    public BuildstoneToolkitForge() {
        IEventBus modbus = Objects.requireNonNull(FMLJavaModLoadingContext.get().getModEventBus());
        EventBuses.registerModEventBus(BuildstoneToolkit.MOD_ID, modbus);

        BuildstoneToolkit.init();

        modbus.addListener(this::onClientSetup);
        modbus.addListener(BuildstoneForgeClientEvents::registerParticleProviders);
    }
    public void onClientSetup(FMLClientSetupEvent event) {
        BuildstoneToolkitClient.init();
    }
}
