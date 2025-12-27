package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.unstable.BuildstonePacketsRegisterPlayerProxyInteraction;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.unstable.BuildstonePacketsRegisterSetProxyTarget;

public class BuildstonePackets {

    public static void register() {
        BuildstonePacketsRegisterSetProxyTarget.register();
        BuildstonePacketsRegisterPlayerProxyInteraction.register();
    }

}
