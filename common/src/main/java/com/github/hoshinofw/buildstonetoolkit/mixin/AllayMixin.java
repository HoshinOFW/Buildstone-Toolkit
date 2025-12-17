package com.github.hoshinofw.buildstonetoolkit.mixin;

import com.github.hoshinofw.buildstonetoolkit.common.level.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.util.ParticleUtil;
import com.github.hoshinofw.buildstonetoolkit.util.Util;
import com.github.hoshinofw.buildstonetoolkit.util.mixin.AllayMixinInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

import static com.github.hoshinofw.buildstonetoolkit.util.Util.*;
import static com.github.hoshinofw.buildstonetoolkit.util.SoundUtil.playAllayConfirmationSound;

@Mixin(Allay.class)
public class AllayMixin extends PathfinderMob implements AllayMixinInterface {

    //TODO Make configurable
    @Unique
    private static List<Double> buildstonetoolkit$allowedDistances = List.of(0.0, 0.5, 1.0, 2.0, 3.0, 5.0, 7.0, 9.0);

    @Unique
    private Util.ValueCycler<Double> buildstonetoolkit$distanceCycler = new Util.ValueCycler<>(
            buildstonetoolkit$allowedDistances,
            this::buildstonetoolkit$getMaxDistanceToOrigin,
            this::buildstonetoolkit$setMaxDistanceToOrigin);

    @Unique private Vec3 buildstonetoolkit$searchOrigin = null;

    @Unique private double buildstonetoolkit$maxDistanceToOrigin = 3.0F;

    protected AllayMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override @Unique
    public @Nullable Vec3 buildstonetoolkit$getSearchOrigin() {
        return buildstonetoolkit$searchOrigin;
    }

    @Unique
    public @Nullable BlockPos buildstonetoolkit$getSearchOriginAsPos() {
        if (buildstonetoolkit$searchOrigin == null) {return null;}
        return BlockPos.containing(buildstonetoolkit$searchOrigin);
    }

    @Override @Unique
    public void buildstonetoolkit$setSearchOrigin(Vec3 value) {
        this.buildstonetoolkit$searchOrigin = value;
    }

    @Override @Unique
    public double buildstonetoolkit$getMaxDistanceToOrigin() {
        return buildstonetoolkit$maxDistanceToOrigin;
    }

    @Override @Unique
    public void buildstonetoolkit$setMaxDistanceToOrigin(double value) {
        this.buildstonetoolkit$maxDistanceToOrigin = value;
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    protected void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack playerIS = player.getItemInHand(interactionHand);
        if (playerIS.getItem() instanceof ModWand) {
            BlockPos selectedPos = Util.getSelectedPos(player);
            BlockPos storedAllaySearchPos = buildstonetoolkit$getSearchOriginAsPos();
            if (!player.isShiftKeyDown()) {
                buildstonetoolkit$setAllaySearchOrigin(player, selectedPos);
            } else {
                if (Util.deepEquals(selectedPos, storedAllaySearchPos)) {
                    if (!player.level().isClientSide()) {
                        buildstonetoolkit$distanceCycler.next();
                        player.displayClientMessage(Component.translatable("message.buildstonetoolkit.allay_distance_set",
                                yellowComponent(Double.toString(buildstonetoolkit$getMaxDistanceToOrigin()))), true);
                    } else {
                        playAllayConfirmationSound(player);
                    }
                } else {
                    Util.setSelectedPos(player, storedAllaySearchPos);
                    if (!player.level().isClientSide()) {
                        player.displayClientMessage(Component.translatable("message.buildstonetoolkit.allay_tracking"), true);
                    } else {
                        if (storedAllaySearchPos != null) {
                            ParticleUtil.spawnSelectionParticle(player, storedAllaySearchPos);}
                        playAllayConfirmationSound(player);
                    }
                }

            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            cir.cancel();
        }
    }

    @Unique
    private void buildstonetoolkit$setAllaySearchOrigin(Player player, BlockPos selectedPos) {
        if (selectedPos != null) {
            buildstonetoolkit$setSearchOrigin(selectedPos.getCenter());
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.buildstonetoolkit.allay_link_success",
                                blueComponent(Arrays.toString(blockPosToArray(selectedPos)))),
                        true);
            } else {
                playAllayConfirmationSound(player);
            }
        } else {
            buildstonetoolkit$setSearchOrigin(null);
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.buildstonetoolkit.allay_link_remove",
                        blueComponent(Component.translatable("message.buildstonetoolkit.allay_link_remove_2").getString())), true);
            } else {
                playAllayConfirmationSound(player);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        Vec3 origin = this.buildstonetoolkit$getSearchOrigin();
        if (origin != null) {compoundTag.putIntArray("searchOrigin", Util.blockPosToArray(BlockPos.containing(origin)));}

        compoundTag.putFloat("maxSearchDistance", (float) this.buildstonetoolkit$getMaxDistanceToOrigin());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.buildstonetoolkit$setSearchOrigin(Util.getSearchOriginFromNBT(compoundTag));
        this.buildstonetoolkit$setMaxDistanceToOrigin(compoundTag.getFloat("maxSearchDistance"));
    }
}
