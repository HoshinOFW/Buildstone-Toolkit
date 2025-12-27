package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.VisionProxyBlockStable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VisionProxyBlock extends VisionProxyBlockStable {
    public VisionProxyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.vision_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }
}
