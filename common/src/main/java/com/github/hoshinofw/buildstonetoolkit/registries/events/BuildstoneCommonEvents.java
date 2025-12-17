package com.github.hoshinofw.buildstonetoolkit.registries.events;


import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.IProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.util.EventUtil;
import com.github.hoshinofw.buildstonetoolkit.util.Util;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static com.github.hoshinofw.buildstonetoolkit.util.ParticleUtil.*;

public class BuildstoneCommonEvents {

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(BuildstoneCommonEvents::onRightClickItem);
    }

    private static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(BuildstoneItems.MOD_WAND.get())) {
            HitResult hit =  player.pick(32, 0.0F, false);
            Level level = player.level();
            if (hit instanceof BlockHitResult blockHitResult) {
                BlockPos hitPos = blockHitResult.getBlockPos();
                if (level.getBlockState(hitPos).getBlock() == Blocks.AIR) {
                    Util.setSelectedPos(player, null);

                } else if (level.getBlockState(hitPos).getBlock() instanceof IProxyBlock proxyBlock) {
                    if (!player.isShiftKeyDown()) {
                        //When not shifting, set proxy link to selection
                        EventUtil.rightClickWandOnProxy(player, proxyBlock, level, hitPos);
                    } else {
                        //When shifting, set selection to the proxy
                        Util.setSelectedPos(player, hitPos);
                        spawnProxyTargetParticle(player, hitPos);
                        spawnProxyParticle(player, hitPos);
                    }
                } else {
                    //Set selection to the arbitrary block.
                    Util.setSelectedPos(player, hitPos);
                    if (level.isClientSide) {
                        spawnSelectionParticle(player, hitPos);
                    }
                }

            } else {
                Util.setSelectedPos(player, null);
            }
        }
        return CompoundEventResult.pass();
    }

}
