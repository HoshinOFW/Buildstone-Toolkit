package com.github.hoshinofw.buildstonetoolkit.content.common.items;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModWand extends Item {

    public static final ResourceLocation MOD_WAND_PROXY_MODE = BuildstoneToolkit.RLFromPath("proxy_mode");

    public ModWand(Properties properties) {
        super(properties);
    }

    public enum Mode {
        OFF,
        ON
    }

    private static ModWand.Mode CLIENT_MODE = ModWand.Mode.OFF;

    public static ModWand.Mode getClientMode() {
        return CLIENT_MODE;
    }

    public static void setClientMode(ModWand.Mode mode) {
        CLIENT_MODE = mode;
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