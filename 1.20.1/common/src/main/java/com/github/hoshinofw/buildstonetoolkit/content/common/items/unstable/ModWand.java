package com.github.hoshinofw.buildstonetoolkit.content.common.items.unstable;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWandStable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModWand extends ModWandStable {

    public ModWand(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.proxy_tuner.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }
}
