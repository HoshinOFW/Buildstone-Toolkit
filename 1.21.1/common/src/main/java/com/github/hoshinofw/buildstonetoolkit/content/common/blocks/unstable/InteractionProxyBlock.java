package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.InteractionProxyBlockStable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class InteractionProxyBlock extends InteractionProxyBlockStable {
    public InteractionProxyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.right_click_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }
}
