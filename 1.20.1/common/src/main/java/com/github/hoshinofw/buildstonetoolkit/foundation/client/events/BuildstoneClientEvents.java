package com.github.hoshinofw.buildstonetoolkit.foundation.client.events;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.ProxyParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.ProxyTargetParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.SelectionParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.util.ClientUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.InteractiveProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.config.BTConfig;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.ProxyInteractionType;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.PlayerProxyInteractionPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.SetProxyTargetPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.SoundUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.SelectionHolder;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.InteractionEvent;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

import static com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util.blockPosToArray;
import static com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util.blueComponent;

public class BuildstoneClientEvents {
    public static boolean wasHovered = false;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(BuildstoneClientEvents::onClientTick);
        InteractionEvent.RIGHT_CLICK_ITEM.register(BuildstoneClientEvents::onRightClickItem);
        InteractionEvent.LEFT_CLICK_BLOCK.register(BuildstoneClientEvents::onLeftClickBlock);
        InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(BuildstoneClientEvents::onLeftClickAir);
        registerMouseScrolled();
    }

    private static void registerMouseScrolled() {
        ClientRawInputEvent.MOUSE_SCROLLED.register((minecraft, y) -> mouseScrolled(minecraft, 0, y));
    }

    private static EventResult mouseScrolled(Minecraft minecraft, double x, double y) {
        LocalPlayer player = minecraft.player;
        if (player == null) return EventResult.pass();
        if ((player.getMainHandItem().getItem() instanceof ModWand || player.getOffhandItem().getItem() instanceof ModWand)
                && player.isShiftKeyDown()) {

            SelectionHolder holder = (SelectionHolder) player;
            if (holder.hasSelectedPos() && minecraft.level != null) {
                assert holder.getSelectedPos() != null;

                Vec3 delta;
                if (y > 0) {
                    delta = player.getLookAngle();
                } else {
                    delta = player.getLookAngle().scale(-1);
                }

                BlockPos newSelection = holder.getSelectedPos().relative(Direction.getNearest(delta.x, delta.y, delta.z));

                holder.setSelectedPos(newSelection);
                SelectionParticle.spawn(minecraft.player, newSelection);
            }

            return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }

    private static void onLeftClickAir(Player player, InteractionHand interactionHand) {
        onLeftClick(player);
    }

    private static EventResult onLeftClickBlock(Player player, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        return onLeftClick(player);
    }

    private static EventResult onLeftClick(@NotNull Player player) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ModWand) {
            SelectionHolder holder = (SelectionHolder) player;
            holder.clearSelection();
            return EventResult.interruptTrue();
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ModWand) {
            SelectionHolder holder = (SelectionHolder) player;
            holder.clearSelection();
        }
        return EventResult.pass();
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null || client.level == null) {
            wasHovered = false;
            return;
        }

        boolean isHovered = client.player.getMainHandItem().getItem() instanceof ModWand;

        if (isHovered && !BuildstoneClientEvents.wasHovered) tunerHovered(client, client.player, client.level);

        BuildstoneClientEvents.wasHovered = isHovered;

        interactionProxyLogic(client, client.player, client.level);
    }

    private static void interactionProxyLogic(Minecraft client, @NotNull LocalPlayer player, ClientLevel level) {
        //TODO Implement my own BlockGetter#traverseBlocks so I can do one iteration and avoid a big array.

        BooleanObjectPair<long[]> pair;
        if (BTConfig.getInteractionProxyRaySkipIsEmpty()) {
            pair = ClientUtil.raycastAndCollectBlocksUntilOccluded(player, level, BTConfig.getInteractionProxyRayReach(),
                    (state, pos) -> state.isAir() || !state.canOcclude(), true);
        } else {
            Set<Block> skipBlocks = BTConfig.getInteractionProxyRaySkipBlocks();
            pair = ClientUtil.raycastAndCollectBlocksUntilOccluded(player, level, BTConfig.getInteractionProxyRayReach(),
                    (state, pos) -> state.isAir() || !state.canOcclude() || skipBlocks.contains(state.getBlock()), true);
        }


        long[] blocks = pair.right();

        LongArrayList ipbs = new LongArrayList();

        ProxyBlockEntity.getRegistry(level).targetIndex.forEachInIfPresent(blocks, (targetedBlock) -> {
            LongCollection pbeCollection = ProxyBlockEntity.getRegistry(level).getProxyLongPosTargeting(BlockPos.of(targetedBlock), InteractiveProxyBlockEntity.class);

            ipbs.addAll(pbeCollection);
        });

        if (client.options.keyUse.isDown()) {
            PlayerProxyInteractionPacket.sendToServer(ipbs.toLongArray(), player.position(),
                    new ProxyInteractionType[]{ProxyInteractionType.RightClickedRightClickProxy, ProxyInteractionType.LookedAtLookingAtProxy});
        } else {
            PlayerProxyInteractionPacket.sendToServer(ipbs.toLongArray(), player.position(),
                    ProxyInteractionType.LookedAtLookingAtProxy);
        }
    }

    private static void tunerHovered(Minecraft client, @NotNull LocalPlayer player, ClientLevel level) {

        SelectionHolder holder = (SelectionHolder) player;

        if (holder.hasSelectedProxyId()) {
            long targetProxyId = holder.getSelectedId();
            TargetFace face = holder.getSelectedFace();
            BlockPos proxyPos = ProxyBlockEntity.getRegistry(level).getProxyPos(targetProxyId);
            if (proxyPos != null) {
                BlockState proxyState = level.getBlockState(proxyPos);

                TargetFace targetParticleFace = proxyState.getBlock() instanceof FaceTargetingProxyBlock fpb
                        && fpb.hasTargetFace(proxyState) ?
                        fpb.getTargetFace(proxyState) : TargetFace.ALL;

                ProxyParticle.spawn(player, targetProxyId, face);
                ProxyTargetParticle.spawn(player, targetProxyId, targetParticleFace);
            }

        } else if (holder.hasSelectedPos()) {
            BlockPos targetPos = holder.getSelectedPos();
            assert targetPos != null;

            if (holder.hasSelectedFace()) {
                SelectionParticle.spawn(player, targetPos, holder.getSelectedFace());
            } else {
                SelectionParticle.spawn(player, targetPos);
            }
        }
    }

    public static CompoundEventResult<ItemStack> onRightClickItem(Player player, InteractionHand hand) {
    Level level = player.level();
    if (level.isClientSide()) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(BuildstoneItems.MOD_WAND.get())) {
            SelectionHolder holder = (SelectionHolder) player;
            BlockPos selectedPos = holder.getSelectedPos();

            BlockHitResult hit;
            if (player.isShiftKeyDown()) {
                hit = ClientUtil.raycastWithTest(player, (BlockPos pos) -> pos.equals(selectedPos), BTConfig.getProxyTunerRayReach());
            } else {
                HitResult hit1 = player.pick(BTConfig.getProxyTunerRayReach(), 0, false);
                if (hit1 instanceof BlockHitResult blockHitResult) {
                    hit = blockHitResult;
                } else {
                    hit = null;
                }
            }
            if (hit != null) {
                BlockPos hitPos = hit.getBlockPos();
                BlockState state = level.getBlockState(hitPos);
                if (state.getBlock() == Blocks.AIR) {
                    //If aimed at the sky
                    holder.clearSelection();

                } else if (state.getBlock() instanceof ProxyBlock proxyBlock) {
                    if (!player.isShiftKeyDown()) {
                        //When not shifting, set proxy link to selection
                        sendSetProxyTargetPacket(holder, player, level, hitPos);
                    } else {
                        //When shifting, set selection to the proxy
                        if (proxyBlock.hasProxyBlockEntity(level, hitPos)) {
                            long proxyId = proxyBlock.getId(level, hitPos);

                            TargetFace face = holder.getSelectedId() == proxyId ? TargetFace.fromDirection(hit.getDirection()) : TargetFace.ALL;

                            TargetFace targetParticleFace = state.getBlock() instanceof FaceTargetingProxyBlock fpb
                                    && fpb.hasTargetFace(state) ?
                                    fpb.getTargetFace(state) : TargetFace.ALL;

                            holder.setSelectedId(proxyId, face);

                            ProxyParticle.spawn(player, proxyId, face);
                            ProxyTargetParticle.spawn(player, proxyId, targetParticleFace);


                        }
                    }
                } else {
                    //When looking at arbitrary block
                    if (player.isShiftKeyDown()) {
                        if (hitPos.equals(holder.getSelectedPos())) {
                            TargetFace face = TargetFace.fromDirection(hit.getDirection());

                            holder.setSelectedPos(hitPos, face);
                            SelectionParticle.spawn(player, hitPos, face);
                        }
                    } else {
                        holder.setSelectedPos(hitPos);
                        SelectionParticle.spawn(player, hitPos);
                    }
                }
            }
        }
    }
        return CompoundEventResult.pass();
    }

    private static void sendSetProxyTargetPacket(SelectionHolder holder, Player player,  Level level, BlockPos hitPos) {
        BlockPos targetPos;
        long proxyId = holder.getSelectedId();
        //Resolve a target pos.

        if (holder.hasSelectedProxyId()) {
            ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(level).getEntry(proxyId);
            if (pbe != null) {
                targetPos = pbe.getBlockPos();
            } else {
                targetPos = holder.getSelectedPos();
            }
        } else {
            targetPos = holder.getSelectedPos();
        }

        if (targetPos != null) {
            //Server will call setLinkedAbsPos and update the client.
            SetProxyTargetPacket.sendToServer(hitPos, targetPos, holder.getSelectedFace());

            SoundUtil.playLinkSuccessSound(player);
            player.displayClientMessage(Component.translatable("message.buildstonetoolkit.link_success",
                            blueComponent(level.getBlockState(targetPos).getBlock().getName().getString()),
                            blueComponent(Arrays.toString(blockPosToArray(targetPos)))),
                    true);
        }
    }
}
