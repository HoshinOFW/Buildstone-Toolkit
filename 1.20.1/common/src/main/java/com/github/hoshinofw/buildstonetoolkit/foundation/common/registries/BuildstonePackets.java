package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.ConfigSyncPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.PlayerProxyInteractionPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.RedstoneProxyCycleModePacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.SetProxyTargetPacket;

public class BuildstonePackets {

    public static void register() {
        SetProxyTargetPacket.HANDLER.register();
        PlayerProxyInteractionPacket.HANDLER.register();
        ConfigSyncPacket.HANDLER.register();
        RedstoneProxyCycleModePacket.HANDLER.register();
    }
}
