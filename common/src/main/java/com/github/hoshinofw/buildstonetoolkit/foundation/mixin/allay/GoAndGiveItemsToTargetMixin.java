package com.github.hoshinofw.buildstonetoolkit.foundation.mixin.allay;


import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.AllayMixinInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.GoAndGiveItemsToTarget;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Mixin(GoAndGiveItemsToTarget.class)
public class GoAndGiveItemsToTargetMixin {

    //TODO: Make configurable
    @Unique
    private static final Item buildstonetoolkit$globalPlayerTrackItem = Items.DIAMOND;

    @Shadow @Final
    private Function<LivingEntity, Optional<PositionTracker>> targetPositionGetter;

    @Shadow
    public static void throwItem(LivingEntity arg, ItemStack arg2, Vec3 arg3) {
    }

    @Shadow
    private static Vec3 getThrowPosition(PositionTracker arg) {return null;}

    @Shadow
    private void triggerDropItemOnBlock(PositionTracker arg, ItemStack arg2, ServerPlayer arg3) {}

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    //Inject at the head. I want the allay to try to find the player near the set origin position, but to default to normal behavior if it can't
    public void tick(ServerLevel serverLevel, LivingEntity entity, long l, CallbackInfo ci) {
        Optional<PositionTracker> optional = this.targetPositionGetter.apply(entity);
        if (optional.isPresent() && entity instanceof Allay allayEntity) {
            PositionTracker positionTracker = optional.get();

            Vec3 allaySearchOrigin = ((AllayMixinInterface) allayEntity).buildstonetoolkit$getSearchOrigin();
            if (allaySearchOrigin == null) {allaySearchOrigin = allayEntity.getEyePosition();}

            if (allayEntity.getMainHandItem().is(buildstonetoolkit$globalPlayerTrackItem)) {
                //TODO Make max match the one in AllayMixin
                ServerPlayer nearestPlayer = (ServerPlayer) Objects.requireNonNull(serverLevel.getNearestPlayer(allaySearchOrigin.x, allaySearchOrigin.y, allaySearchOrigin.z, 20, true));
                Vec3 nearestPlayerPos = nearestPlayer.getEyePosition();
                double d = nearestPlayerPos.distanceTo(allaySearchOrigin);
                if (d < ((AllayMixinInterface) allayEntity).buildstonetoolkit$getMaxDistanceToOrigin()) {
                    ItemStack itemStack = allayEntity.getInventory().removeItem(0, 1);
                    if (!itemStack.isEmpty()) {
                        throwItem(allayEntity, itemStack, getThrowPosition(positionTracker));
                        this.triggerDropItemOnBlock(positionTracker, itemStack, nearestPlayer);

                        allayEntity.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 60);
                    }
                }
            } else {
                double d = positionTracker.currentPosition().distanceTo(allaySearchOrigin);
                if (d < ((AllayMixinInterface) allayEntity).buildstonetoolkit$getMaxDistanceToOrigin()) {
                    ItemStack itemStack = allayEntity.getInventory().removeItem(0, 1);
                    if (!itemStack.isEmpty()) {
                        throwItem(allayEntity, itemStack, getThrowPosition(positionTracker));
                        AllayAi.getLikedPlayer(allayEntity).ifPresent((arg3) -> this.triggerDropItemOnBlock(positionTracker, itemStack, arg3));

                        allayEntity.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 60);
                    }
                }
            }
            ci.cancel();
        }
    }

}