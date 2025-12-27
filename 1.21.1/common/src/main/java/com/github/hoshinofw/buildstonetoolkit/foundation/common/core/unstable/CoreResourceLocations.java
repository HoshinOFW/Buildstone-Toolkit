package com.github.hoshinofw.buildstonetoolkit.foundation.common.core.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.Overwrite;
import net.minecraft.resources.ResourceLocation;

@Overwrite
public class CoreResourceLocations {
    public static final ResourceLocation MOD_WAND_PROXY_MODE = ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "proxy_mode");
}
