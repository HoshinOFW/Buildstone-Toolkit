package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.PlayerProxyInteractionPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.SetAllayTargetPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.SetProxyTargetPacket;

public class BuildstonePackets {

    public static void register() {
        SetProxyTargetPacket.register();
        PlayerProxyInteractionPacket.register();
        SetAllayTargetPacket.register();
    }

}
