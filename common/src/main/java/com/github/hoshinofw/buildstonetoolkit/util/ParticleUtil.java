package com.github.hoshinofw.buildstonetoolkit.util;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.IProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle.BlockParticle;
import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneItems;
import com.github.hoshinofw.buildstonetoolkit.util.client.BlockParticleTexture;
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

    public static BooleanSupplier targetParticleShouldPersist(@NotNull Player player, @NotNull BlockPos targetPos) {
        return () -> (player.getItemBySlot(EquipmentSlot.MAINHAND).is(BuildstoneItems.MOD_WAND.get())
                || player.getItemBySlot(EquipmentSlot.OFFHAND).is(BuildstoneItems.MOD_WAND.get()))
                && Util.getSelectedPos(player) == targetPos;
    }
    public static void spawnSelectionParticle(@NotNull Player player, @NotNull BlockPos targetPos) {
        if (player.level() instanceof ClientLevel clientLevel) {
            Particle particle = BlockParticle.create(clientLevel, targetPos)
                .setRGBATint(1, 1, 1, 0.85F)
                .setSize(1F)
                .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK)
                .setPersistSupplier(targetParticleShouldPersist(player, targetPos));
            //BuildstoneToolkit.LOGGER.info("Summoned Target Particle: {} at: {}", particle, targetPos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    public static void spawnProxyParticle(@NotNull Player player, @NotNull BlockPos proxyPos) {
        if (player.level().getBlockState(proxyPos).getBlock() instanceof IProxyBlock proxyBlock) {
            if (player.level() instanceof ClientLevel clientLevel) {
                BlockPos targetPos = proxyBlock.getLinkedBlockPos(player.level(), proxyPos);
                if (targetPos == null) {return;}
                Particle particle = BlockParticle.create(clientLevel, proxyPos)
                        .setRGBATint(1, 1, 1, 0.85F)
                        .setSize(1F)
                        .setTextureIndex(BlockParticleTexture.PROXY_BLOCK)
                        .setPersistSupplier(targetParticleShouldPersist(player, proxyPos));
                //BuildstoneToolkit.LOGGER.info("Summoned Proxy Particle: {} at: {}", particle, proxyPos);
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }

    public static void spawnProxyTargetParticle(@NotNull Player player, @NotNull BlockPos proxyPos) {
        if (player.level().getBlockState(proxyPos).getBlock() instanceof IProxyBlock proxyBlock) {
            BlockPos targetPos = proxyBlock.getLinkedBlockPos(player.level(), proxyPos);
            if (targetPos == null) {return;}
            if (player.level() instanceof ClientLevel clientLevel) {
                Particle particle = BlockParticle.create(clientLevel, targetPos)
                        .setRGBATint(1, 1, 1, 0.85F)
                        .setSize(1F)
                        .setTextureIndex(BlockParticleTexture.PROXY_TARGET_BLOCK)
                        .setPersistSupplier(targetParticleShouldPersist(player, proxyPos));
                //BuildstoneToolkit.LOGGER.info("Summoned Proxy Target Particle: {} at: {}", particle, targetPos);
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }
}
