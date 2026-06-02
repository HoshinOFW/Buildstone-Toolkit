package com.github.hoshinofw.buildstonetoolkit.content.common.items;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class ModWand extends Item {

    @OverwriteVersion
    public static final ResourceLocation MOD_WAND_PROXY_MODE = ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "proxy_mode");

    @ShadowVersion
    public ModWand(Properties properties) {
        super(properties);
    }

    @ShadowVersion
    @ModifySignature("appendHoverText")
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag);

}
