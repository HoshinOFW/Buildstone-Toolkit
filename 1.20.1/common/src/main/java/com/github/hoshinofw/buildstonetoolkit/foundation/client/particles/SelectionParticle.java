package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.util.RenderUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.SelectionHolder;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.BlockParticleTexture;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

public class SelectionParticle {

    public static BiFunction<Player, BlockPos, Vec3Supplier> activeSelectionParticlePosSupplier = SelectionParticle::createSelectionParticlePosSupplier;
    public static BiFunction<Player, BlockPos, RenderTransformSupplier> activeSelectionParticleRenderTransformSupplier = SelectionParticle::createSelectionParticleRenderTransformSupplier;

    public static BooleanSupplier createSelectionParticleShouldPersist(@NotNull Player player, @NotNull BlockPos targetPos) {
        SelectionHolder holder = (SelectionHolder) player;
        return () -> (player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                && holder.getSelectedPos() == targetPos;
    }

    public static Vec3Supplier createSelectionParticlePosSupplier(@NotNull Player player, @NotNull BlockPos pos) {
        return pos::getCenter;
    }

    public static RenderTransformSupplier createSelectionParticleRenderTransformSupplier(@NotNull Player player, @NotNull BlockPos pos) {
        return (float partialTicks) -> null;
    }

    public static void spawn(@NotNull Player player, @NotNull BlockPos pos) {
        if (player.level() instanceof ClientLevel clientLevel) {
            Particle particle = CubeParticle.create(clientLevel, pos)
                    .setRGBATint(1, 1, 1, 0.85F)
                    .setSize(1F)
                    .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK)
                    .setPersistSupplier(createSelectionParticleShouldPersist(player, pos))
                    .setPosSupplier(activeSelectionParticlePosSupplier.apply(player, pos))
                    .setRenderTransformContextSupplier(activeSelectionParticleRenderTransformSupplier.apply(player, pos));
            //BuildstoneToolkit.LOGGER.info("Summoned Target Particle: {} at: {}", particle, pos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    public static void spawn(@NotNull Player player, @NotNull BlockPos pos, TargetFace face) {
        if (player.level() instanceof ClientLevel clientLevel) {
            Particle particle = CubeParticle.create(clientLevel, pos)
                    .setRGBATint(1, 1, 1, 0.85F)
                    .setScale(RenderUtil.FaceTargetRender.getScaleForFace(face))
                    .setRenderOffset(RenderUtil.FaceTargetRender.getOffsetForFace(face))
                    .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK)
                    .setPersistSupplier(createSelectionParticleShouldPersist(player, pos))
                    .setPosSupplier(activeSelectionParticlePosSupplier.apply(player, pos))
                    .setRenderTransformContextSupplier(activeSelectionParticleRenderTransformSupplier.apply(player, pos));
            //BuildstoneToolkit.LOGGER.info("Summoned Target Particle: {} at: {}", particle, pos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    public static void setActiveSelectionParticlePosSupplier(BiFunction<Player, BlockPos, Vec3Supplier> activeSelectionParticlePosSupplier) {
        SelectionParticle.activeSelectionParticlePosSupplier = activeSelectionParticlePosSupplier;
    }

    public static void setActiveSelectionParticleRenderTransformSupplier(BiFunction<Player, BlockPos, RenderTransformSupplier> activeSelectionParticleRenderTransformSupplier) {
        SelectionParticle.activeSelectionParticleRenderTransformSupplier = activeSelectionParticleRenderTransformSupplier;
    }
}
