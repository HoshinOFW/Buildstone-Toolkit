package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.allay;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.unstable.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable.SetAllayTargetPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.PlayerUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.AllayMixinInterface;
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

import static com.github.hoshinofw.buildstonetoolkit.foundation.common.util.SoundUtil.playAllayConfirmationSound;
import static com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util.blueComponent;
import static com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util.yellowComponent;

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
            if (!player.isShiftKeyDown()) {
                if (player.level().isClientSide()) {
                    BlockPos selectedPos = PlayerUtil.getSelectedPos(player);
                    buildstonetoolkit$setAllaySearchOrigin(player, selectedPos);
                    if (selectedPos != null) {
                        SetAllayTargetPacket.sendToServer(this.getId(), selectedPos);
                    } else {
                        SetAllayTargetPacket.sendToServer(this.getId(), BlockPos.containing(this.position()));
                    }
                }
            } else {
                if (!player.level().isClientSide()) {
                    buildstonetoolkit$distanceCycler.next();
                    player.displayClientMessage(Component.translatable("message.buildstonetoolkit.allay_distance_set",
                            yellowComponent(Double.toString(buildstonetoolkit$getMaxDistanceToOrigin()))), true);
                } else {
                    playAllayConfirmationSound(player);
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
                                Util.blueComponent(Arrays.toString(Util.blockPosToArray(selectedPos)))),
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
        this.buildstonetoolkit$setSearchOrigin(NBTUtil.getSearchOriginFromNBT(compoundTag));
        this.buildstonetoolkit$setMaxDistanceToOrigin(compoundTag.getFloat("maxSearchDistance"));
    }
}
