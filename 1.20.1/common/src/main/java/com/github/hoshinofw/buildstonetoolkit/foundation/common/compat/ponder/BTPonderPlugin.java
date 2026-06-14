package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.ponder;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import dev.architectury.registry.registries.DeferredSupplier;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class BTPonderPlugin implements PonderPlugin {

    public static final ResourceLocation BUILDSTONE_TOOLKIT = BuildstoneToolkit.RLFromPath("ponder_tag_buildstone_toolkit");

    @Override
    public @NotNull String getModId() {
        return BuildstoneToolkit.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<DeferredSupplier<Item>> HELPER = helper.withKeyFunction(DeferredSupplier::getId);

        HELPER.addStoryBoard(BuildstoneItems.OBSERVER_PROXY, "observer_proxy", PonderScenes::observerProxy, BUILDSTONE_TOOLKIT);

    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(BUILDSTONE_TOOLKIT)
                .item(BuildstoneItems.MOD_WAND.get(), true, false)
                .title(Component.translatable("buildstonetoolkit.ponder.tag.main.title").toString())
                .description(Component.translatable("buildstonetoolkit.ponder.tag.main.description").toString())
                .addToIndex()
                .register();

        helper.addToTag(BuildstoneItems.OBSERVER_PROXY.getId());

    }
}
