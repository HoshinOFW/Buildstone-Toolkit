package com.github.hoshinofw.buildstonetoolkit.foundation.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.IdProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.render.BlockParticleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class ParticleUtil {

    //TODO replace texture indexes with enums

    public static BooleanSupplier selectionParticleShouldPersist(@NotNull Player player, @NotNull BlockPos targetPos) {
        return () -> (player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                && PlayerUtil.getSelectedPos(player) == targetPos;
    }

    public static BooleanSupplier createIdProxyParticleShouldPersist(@NotNull Player player, long proxyId) {
        return () -> ((player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                && PlayerUtil.getSelectedProxyId(player) == proxyId);
    }

    public static BlockPosSupplier createIdProxyParticlePosSupplier(@NotNull Player player, long proxyId) {
        return () -> {
            IdProxyBlockEntity<?> pbe = IdProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbe == null) return null;
            return pbe.getBlockPos();
        };
    }

    public static BlockPosSupplier createIdProxyParticleTargetPosSupplier(@NotNull Player player, long proxyId) {
        return () -> {
            IdProxyBlockEntity<?> pbe = IdProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbe == null) return null;
            return pbe.getLinkedAbsPos();
        };
    }

    public static void spawnIdProxyParticle(@NotNull Player player, @NotNull BlockPos proxyPos, long proxyId) {
        if (player.level().getBlockState(proxyPos).getBlock() instanceof IdProxyBlock<?> idProxyBlock) {
            if (player.level() instanceof ClientLevel clientLevel) {
                Particle particle = CubeParticle.create(clientLevel, proxyPos)
                        .setRGBATint(1, 1, 1, 0.85F)
                        .setSize(1F)
                        .setTextureIndex(BlockParticleTexture.PROXY_BLOCK)
                        .setPersistSupplier(createIdProxyParticleShouldPersist(player, proxyId))
                        .setPosSupplier(createIdProxyParticlePosSupplier(player, proxyId));
                //BuildstoneToolkit.LOGGER.info("Summoned Proxy Target Particle: {} at: {}", particle, targetPos);
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }

    public static void spawnIdProxyParticle(@NotNull Player player, long proxyId) {
        IdProxyBlockEntity<?> idbe = IdProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (idbe == null) {
            //BuildstoneToolkit.LOGGER.info("spawnIdProxyParticle failed because id: {} not found in idRegistry", proxyId);
            return;
        }
        spawnIdProxyParticle(player, idbe.getBlockPos(), proxyId);
    }

    public static void spawnIdProxyTargetParticle(@NotNull Player player, @NotNull BlockPos proxyPos, long proxyId) {
        if (player.level().getBlockState(proxyPos).getBlock() instanceof IdProxyBlock<?> idProxyBlock) {
            if (player.level() instanceof ClientLevel clientLevel) {
                BlockPos targetPos = idProxyBlock.getLinkedAbsPos(clientLevel, proxyPos);
                Particle particle = CubeParticle.create(clientLevel, targetPos)
                        .setRGBATint(1, 1, 1, 0.85F)
                        .setSize(1F)
                        .setTextureIndex(BlockParticleTexture.PROXY_TARGET_BLOCK)
                        .setPersistSupplier(createIdProxyParticleShouldPersist(player, proxyId))
                        .setPosSupplier(createIdProxyParticleTargetPosSupplier(player, proxyId));
                //BuildstoneToolkit.LOGGER.info("Summoned Proxy Target Particle: {} at: {}", particle, targetPos);
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }

    public static void spawnIdProxyTargetParticle(@NotNull Player player, long proxyId) {
        IdProxyBlockEntity<?> idbe = IdProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (idbe == null) {
            //BuildstoneToolkit.LOGGER.info("spawnIdProxyTargetParticle failed because id: {} not found in idRegistry", proxyId);
            return;
        }
        spawnIdProxyTargetParticle(player, idbe.getBlockPos(), proxyId);
    }

    public static void spawnSelectionParticle(@NotNull Player player, @NotNull BlockPos targetPos) {
        if (player.level() instanceof ClientLevel clientLevel) {
            Particle particle = CubeParticle.create(clientLevel, targetPos)
                .setRGBATint(1, 1, 1, 0.85F)
                .setSize(1F)
                .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK)
                .setPersistSupplier(selectionParticleShouldPersist(player, targetPos));
            //BuildstoneToolkit.LOGGER.info("Summoned Target Particle: {} at: {}", particle, targetPos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    public static void spawnProxyParticle(@NotNull Player player, @NotNull BlockPos proxyPos) {
        if (player.level().getBlockState(proxyPos).getBlock() instanceof ProxyBlock) {
            if (player.level() instanceof ClientLevel clientLevel) {
                Particle particle = CubeParticle.create(clientLevel, proxyPos)
                        .setRGBATint(1, 1, 1, 0.85F)
                        .setSize(1F)
                        .setTextureIndex(BlockParticleTexture.PROXY_BLOCK)
                        .setPersistSupplier(selectionParticleShouldPersist(player, proxyPos));
                //BuildstoneToolkit.LOGGER.info("Summoned Proxy Particle: {} at: {}", particle, proxyPos);
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }

    public static void spawnProxyTargetParticle(@NotNull Player player, @NotNull BlockPos proxyPos) {
        if (player.level().getBlockState(proxyPos).getBlock() instanceof ProxyBlock proxyBlock) {
            if (player.level() instanceof ClientLevel clientLevel) {
                BlockPos targetPos = proxyBlock.getLinkedAbsPos(clientLevel, proxyPos);
                Particle particle = CubeParticle.create(clientLevel, targetPos)
                        .setRGBATint(1, 1, 1, 0.85F)
                        .setSize(1F)
                        .setTextureIndex(BlockParticleTexture.PROXY_TARGET_BLOCK)
                        .setPersistSupplier(selectionParticleShouldPersist(player, proxyPos));
                //BuildstoneToolkit.LOGGER.info("Summoned Proxy Target Particle: {} at: {}", particle, targetPos);
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }
}
