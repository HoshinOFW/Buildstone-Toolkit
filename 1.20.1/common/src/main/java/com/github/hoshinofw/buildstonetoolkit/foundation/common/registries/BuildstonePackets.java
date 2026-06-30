package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.ConfigSyncPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.PlayerProxyInteractionPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.RedstoneProxyCycleModePacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.SetProxyTargetPacket;

public class BuildstonePackets {

    public static void register() {
        SetProxyTargetPacket.HANDLER.registerReceiver();
        PlayerProxyInteractionPacket.HANDLER.registerReceiver();
        RedstoneProxyCycleModePacket.HANDLER.registerReceiver();

        if (!BuildstoneToolkit.isDedicatedServer()) {
            ConfigSyncPacket.HANDLER.registerReceiver();
        }
    }
}
