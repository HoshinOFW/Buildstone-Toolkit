package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.ClientEventUtil;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BuildstoneCommonEvents {

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(BuildstoneCommonEvents::onRightClickItem);
    }
    private static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return ClientEventUtil.onRightClickItem(player, hand);
        } else {
            return CompoundEventResult.pass();
        }
    }

}
