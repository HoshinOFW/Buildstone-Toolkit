package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.events;


import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.IdProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.EventUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.ParticleUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.PlayerUtil;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BuildstoneCommonEvents {

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(BuildstoneCommonEvents::onRightClickItem);
    }

    //TODO This method is a mess.........
    private static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand) {
        if (player.level() instanceof ClientLevel clientLevel) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(BuildstoneItems.MOD_WAND.get())) {
                HitResult hit =  player.pick(96, 0.0F, false);
                if (hit instanceof BlockHitResult blockHitResult) {
                    BlockPos hitPos = blockHitResult.getBlockPos();
                    if (clientLevel.getBlockState(hitPos).getBlock() == Blocks.AIR) {
                        //If aimed at the sky
                        PlayerUtil.setSelectedPos(player, null);
                        PlayerUtil.setSelectedProxyId(player, -1L);

                    } else if (clientLevel.getBlockState(hitPos).getBlock() instanceof ProxyBlock proxyBlock) {
                        if (!player.isShiftKeyDown()) {
                            //When not shifting, set proxy link to selection
                            EventUtil.rightClickWandOnProxy(player, proxyBlock, clientLevel, hitPos);

                        } else {
                            //When shifting, set selection to the proxy
                            //Different logic depending on if the proxy is an idProxy or not.
                            if (proxyBlock instanceof IdProxyBlock<?> idProxyBlock) {
                                //If IdProxy, set id to it
                                long proxyId = idProxyBlock.getId(clientLevel, hitPos);
                                PlayerUtil.setSelectedPos(player, null);
                                PlayerUtil.setSelectedProxyId(player, proxyId);

                                ParticleUtil.spawnIdProxyTargetParticle(player, hitPos, proxyId);
                                ParticleUtil.spawnIdProxyParticle(player, hitPos, proxyId);

                            } else {
                                //Else just set position
                                PlayerUtil.setSelectedPos(player, hitPos);
                                PlayerUtil.setSelectedProxyId(player, -1);

                                ParticleUtil.spawnProxyTargetParticle(player, hitPos);
                                ParticleUtil.spawnProxyParticle(player, hitPos);
                            }
                        }
                    } else {
                        //Arbitrary block logic
                        if (player.isShiftKeyDown()) {
                            //If shifting, clear selection (we checked if it was a proxy earlier)
                            PlayerUtil.setSelectedPos(player, null);
                            PlayerUtil.setSelectedProxyId(player, -1);
                        } else {
                            //If not shifting, set selection to arbitrary hitPos
                            PlayerUtil.setSelectedPos(player, hitPos);
                            PlayerUtil.setSelectedProxyId(player, -1);
                            if (clientLevel.isClientSide) {
                                ParticleUtil.spawnSelectionParticle(player, hitPos);
                            }
                        }
                    }

                } else {
                    //Just in case something goes weird
                    PlayerUtil.setSelectedPos(player, null);
                    PlayerUtil.setSelectedProxyId(player, -1);
                }
            }
        }
        return CompoundEventResult.pass();
    }

}
