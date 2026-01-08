package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

public class SoundUtil {

    public static void playAllayConfirmationSound(Player player) {
        player.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 0.5F, 1.5F);
    }

    public static void playLinkSuccessSound(Player player) {
        player.playSound(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 0.8F, 2F);
    }
}
