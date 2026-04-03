package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.ponder;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import dev.architectury.registry.registries.DeferredSupplier;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public abstract class BTPonderPlugin implements PonderPlugin {

    @OverwriteVersion
    public static final ResourceLocation BUILDSTONE_TOOLKIT = ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "ponder_tag_buildstone_toolkit");

    @ShadowVersion
    @Override
    public @NotNull String getModId() {
        return BuildstoneToolkit.MOD_ID;
    }
}
