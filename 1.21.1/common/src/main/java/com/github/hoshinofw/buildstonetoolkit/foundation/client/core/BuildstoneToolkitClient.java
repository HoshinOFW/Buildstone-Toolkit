package com.github.hoshinofw.buildstonetoolkit.foundation.client.core;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable.SableClientCompatInit;
import com.github.hoshinofw.multiversion.OverwriteVersion;


public class BuildstoneToolkitClient {

    @OverwriteVersion
    public static void sableCompatPostInit() {
        SableClientCompatInit.init();
    }
}
