package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.util.RenderUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders.SelectionHolder;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.BlockParticleTexture;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

public class ProxyParticle {

    public static @NotNull TriFunction<Player, Long, TargetFace, BooleanSupplier> activeProxyParticlePersistSupplier = ProxyParticle::createPersistSupplier;
    public static @NotNull BiFunction<Player, Long, Vec3Supplier> activeProxyParticlePosSupplier = ProxyParticle::createPosSupplier;
    public static @NotNull BiFunction<Player, Long, RenderTransformSupplier> activeProxyParticleRenderTransformSupplier = ProxyParticle::createRTS;

    public static BooleanSupplier createPersistSupplier(@NotNull Player player, long proxyId, TargetFace face) {
        SelectionHolder holder = (SelectionHolder) player;
        return () -> ((player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                && holder.getSelectedId() == proxyId
                && face == holder.getSelectedFace());
    }

    public static Vec3Supplier createPosSupplier(@NotNull Player player, long proxyId) {
        return () -> {
            ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbe == null) return null;
            return pbe.getBlockPos().getCenter();
        };
    }

    public static RenderTransformSupplier createRTS(@NotNull Player player, long proxyId) {
        return (float partialTicks) -> null;
    }

    public static void spawn(@NotNull Player player, long proxyId, TargetFace face) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            //BuildstoneToolkit.LOGGER.info("spawnIdProxyParticle failed because id: {} not found in idRegistry", proxyId);
            return;
        }
        if (player.level() instanceof ClientLevel clientLevel) {
            Particle particle = CubeParticle.create(clientLevel, pbe.getBlockPos())
                    .setRGBATint(1, 1, 1, 0.85F)
                    .setTextureIndex(BlockParticleTexture.PROXY_BLOCK)
                    .setScale(RenderUtil.FaceTargetRender.getScaleForFace(face))
                    .setRenderOffset(RenderUtil.FaceTargetRender.getOffsetForFace(face))
                    .setPersistSupplier(activeProxyParticlePersistSupplier.apply(player, proxyId, face))
                    .setPosSupplier(activeProxyParticlePosSupplier.apply(player, proxyId))
                    .setRenderTransformContextSupplier(activeProxyParticleRenderTransformSupplier.apply(player, proxyId))
                    .build();

            //BuildstoneToolkit.LOGGER.info("Summoned Proxy Target Particle: {} at: {}", particle, targetPos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    public static void setActivePersistSupplier(@NotNull TriFunction<Player, Long, TargetFace, BooleanSupplier> activeProxyParticlePersistSupplier) {
        ProxyParticle.activeProxyParticlePersistSupplier = activeProxyParticlePersistSupplier;
    }

    public static void setActivePosSupplier(@NotNull BiFunction<Player, Long, Vec3Supplier> activeProxyParticlePosSupplier) {
        ProxyParticle.activeProxyParticlePosSupplier = activeProxyParticlePosSupplier;
    }

    public static void setActiveRTS(@NotNull BiFunction<Player, Long, RenderTransformSupplier> activeProxyParticleRenderTransformSupplier) {
        ProxyParticle.activeProxyParticleRenderTransformSupplier = activeProxyParticleRenderTransformSupplier;
    }
}
