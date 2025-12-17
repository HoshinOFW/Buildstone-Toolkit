package com.github.hoshinofw.buildstonetoolkit.content.common.items;

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

public class ModWand extends Item {

    @Environment(EnvType.CLIENT)
    public enum Mode {
        OFF,
        ON
    }

    @Environment(EnvType.CLIENT)
    private static Mode CLIENT_MODE = Mode.OFF;

    @Environment(EnvType.CLIENT)
    public static Mode getClientMode() {
        return CLIENT_MODE;
    }

    @Environment(EnvType.CLIENT)
    public static void setClientMode(Mode mode) {
        CLIENT_MODE = mode;
    }

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
