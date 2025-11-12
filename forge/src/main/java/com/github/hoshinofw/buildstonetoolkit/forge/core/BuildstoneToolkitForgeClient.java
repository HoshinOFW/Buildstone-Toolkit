package com.github.hoshinofw.buildstonetoolkit.forge.core;

import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkitClient;
import com.github.hoshinofw.buildstonetoolkit.forge.registries.BuildstoneForgeClientEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.util.Objects;

@Mod(value = BuildstoneToolkit.MOD_ID)
public class BuildstoneToolkitForgeClient {

    public BuildstoneToolkitForgeClient() {
        IEventBus modbus = Objects.requireNonNull(FMLJavaModLoadingContext.get().getModEventBus());

        modbus.register(BuildstoneForgeClientEvents.class);
        modbus.addListener(BuildstoneToolkitForgeClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        BuildstoneToolkitClient.init();
    }

}
