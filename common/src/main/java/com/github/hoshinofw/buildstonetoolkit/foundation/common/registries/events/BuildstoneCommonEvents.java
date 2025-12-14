package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events;


import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.EventUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.ParticleUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
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

public class BuildstoneCommonEvents {

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(BuildstoneCommonEvents::onRightClickItem);
    }
    //TODO Add multi-target capabilities
    private static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(BuildstoneItems.MOD_WAND.get())) {
            HitResult hit =  player.pick(32, 0.0F, false);
            Level level = player.level();
            if (hit instanceof BlockHitResult blockHitResult) {
                BlockPos hitPos = blockHitResult.getBlockPos();
                if (level.getBlockState(hitPos).getBlock() == Blocks.AIR) {
                    Util.setSelectedPos(player, null);

                } else if (level.getBlockState(hitPos).getBlock() instanceof ProxyBlock proxyBlock) {
                    if (!player.isShiftKeyDown()) {
                        //When not shifting, set proxy link to selection
                        EventUtil.rightClickWandOnProxy(player, proxyBlock, level, hitPos);
                    } else {
                        //When shifting, set selection to the proxy
                        Util.setSelectedPos(player, hitPos);
                        ParticleUtil.spawnProxyTargetParticle(player, hitPos);
                        ParticleUtil.spawnProxyParticle(player, hitPos);
                    }
                } else {
                    //Set selection to the arbitrary block.
                    Util.setSelectedPos(player, hitPos);
                    if (level.isClientSide) {
                        ParticleUtil.spawnSelectionParticle(player, hitPos);
                    }
                }

            } else {
                Util.setSelectedPos(player, null);
            }
        }
        return CompoundEventResult.pass();
    }

}
