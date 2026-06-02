package com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;

public class SableClientCompatInit {

    public static void init() {
        SableParticleCompat.init();
        BuildstoneToolkit.LOGGER.info("Sable Client Compat initialization done!");
    }

}
