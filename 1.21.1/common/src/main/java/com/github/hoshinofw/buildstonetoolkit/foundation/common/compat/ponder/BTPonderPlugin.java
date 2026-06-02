package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.ponder;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.minecraft.resources.ResourceLocation;

public abstract class BTPonderPlugin implements PonderPlugin {

    @OverwriteVersion
    public static final ResourceLocation BUILDSTONE_TOOLKIT = ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "ponder_tag_buildstone_toolkit");

}
