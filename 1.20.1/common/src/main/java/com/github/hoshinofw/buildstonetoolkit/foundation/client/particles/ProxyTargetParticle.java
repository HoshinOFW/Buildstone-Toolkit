package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.util.RenderUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdRegistry;
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
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

public class ProxyTargetParticle {

    public static TriFunction<Player, Long, TargetFace, BooleanSupplier> activeProxyTargetParticlePersistSupplier = ProxyTargetParticle::createProxyTargetParticleShouldPersist;
    public static BiFunction<Player, Long, Vec3Supplier> activeProxyTargetParticlePosSupplier = ProxyTargetParticle::createProxyTargetParticlePosSupplier;
    public static BiFunction<Player, Long, RenderTransformSupplier> activeProxyTargetParticleRenderTransformSupplier = ProxyTargetParticle::createProxyTargetParticleRenderTransformSupplier;

    public static BooleanSupplier createProxyTargetParticleShouldPersist(Player player, long proxyId, TargetFace face) {
        IdRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(player.level()).idRegistry;
        SelectionHolder holder = (SelectionHolder) player;

        ProxyBlockEntity<?, ?> pbe = registry.getEntry(proxyId);
        if (pbe == null) return () -> false;
        if (pbe.getBlockState().getBlock() instanceof FaceTargetingProxyBlock ftpb) {
            return () -> {
                ProxyBlockEntity<?, ?> pbeLocal = registry.getEntry(proxyId);
                if (pbeLocal == null) {
                    BuildstoneToolkit.LOGGER.info("I am here");
                    return true;
                }

                return ((player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                        || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                        && holder.getSelectedId() == proxyId && ftpb.getTargetFace(pbeLocal.getBlockState()) == face);

            };
        }

        return () -> ((player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                && holder.getSelectedId() == proxyId);
    }

    public static Vec3Supplier createProxyTargetParticlePosSupplier(@NotNull Player player, long proxyId) {
        return () -> {
            ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbe == null) return null;
            return pbe.getLinkedAbsPos().getCenter();
        };
    }

    public static RenderTransformSupplier createProxyTargetParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        return (float partialTicks) -> null;
    }

    public static void spawn(@NotNull Player player, long proxyId, TargetFace face) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {return;}
        if (player.level() instanceof ClientLevel clientLevel) {
            BlockPos targetPos = pbe.getLinkedAbsPos();
            Particle particle = CubeParticle.create(clientLevel, targetPos)
                    .setRGBATint(1, 1, 1, 0.85F)
                    .setScale(RenderUtil.FaceTargetRender.getScaleForFace(face))
                    .setRenderOffset(RenderUtil.FaceTargetRender.getOffsetForFace(face))
                    .setTextureIndex(BlockParticleTexture.PROXY_TARGET_BLOCK)
                    .setPersistSupplier(ProxyTargetParticle.activeProxyTargetParticlePersistSupplier.apply(player, proxyId, face))
                    .setPosSupplier(activeProxyTargetParticlePosSupplier.apply(player, proxyId))
                    .setRenderTransformContextSupplier(activeProxyTargetParticleRenderTransformSupplier.apply(player, proxyId));
            //BuildstoneToolkit.LOGGER.info("Summoned Proxy Target Particle: {} at: {}", particle, targetPos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    public static void setActiveProxyTargetParticlePosSupplier(BiFunction<Player, Long, Vec3Supplier> activeProxyTargetParticlePosSupplier) {
        ProxyTargetParticle.activeProxyTargetParticlePosSupplier = activeProxyTargetParticlePosSupplier;
    }

    public static void setActiveProxyTargetParticleRenderTransformSupplier(BiFunction<Player, Long, RenderTransformSupplier> activeProxyTargetParticleRenderTransformSupplier) {
        ProxyTargetParticle.activeProxyTargetParticleRenderTransformSupplier = activeProxyTargetParticleRenderTransformSupplier;
    }
}
