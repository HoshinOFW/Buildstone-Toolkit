package com.github.hoshinofw.buildstonetoolkit.forge.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.forge.GameInstanceImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuildstoneToolkit.MOD_ID)
public final class BuildstoneToolkitForge {
    public static IEventBus MOD_BUS;

    public BuildstoneToolkitForge() {
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(BuildstoneToolkit.MOD_ID, MOD_BUS);
        BuildstoneToolkit.setIsDedicatedServer(Platform.getEnv().isDedicatedServer());
        BuildstoneToolkit.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BuildstoneToolkitForgeClient::init);


    }

}
