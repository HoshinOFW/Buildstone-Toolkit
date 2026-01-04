package com.github.hoshinofw.buildstonetoolkit.forge.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.platform.forge.EventBuses;
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

        BuildstoneToolkit.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BuildstoneToolkitForgeClient::init);
    }

}
