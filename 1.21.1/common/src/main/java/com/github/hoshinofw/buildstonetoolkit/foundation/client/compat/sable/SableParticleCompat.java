package com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable.particle.*;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.ProxyParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.ProxyTargetParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.SelectionParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SableParticleCompat {
    //TODO Add smart persist suppliers that know to return false if the block has been assembled.

    public static void init() {
        SelectionParticle.setActivePosSupplier(SableParticleCompat::createExpensiveSelectionParticlePosSupplier);
        SelectionParticle.setActiveRTS(SableParticleCompat::createExpensiveSelectionParticleRenderTransformSupplier);

        ProxyParticle.setActivePosSupplier(SableParticleCompat::createExpensiveProxyParticlePosSupplier);
        ProxyParticle.setActiveRTS(SableParticleCompat::createExpensiveProxyParticleRenderTransformSupplier);

        ProxyTargetParticle.setActivePosSupplier(SableParticleCompat::createExpensiveProxyTargetParticlePosSupplier);
        ProxyTargetParticle.setActiveRTS(SableParticleCompat::createExpensiveProxyTargetParticleRenderTransformSupplier);

    }

    public static Vec3Supplier createExpensiveSelectionParticlePosSupplier(@NotNull Player player, BlockPos pos) {
        return new SableSelectionParticle.PosSupplier(player, pos);
    }

    public static RenderTransformSupplier createExpensiveSelectionParticleRenderTransformSupplier(@NotNull Player player, BlockPos pos) {
        RenderTransformSupplier baseSupplier = SelectionParticle.createRTS(player, pos);
        return new SableSelectionParticle.RenderTransform(pos, baseSupplier);
    }

    public static Vec3Supplier createExpensiveProxyParticlePosSupplier(@NotNull Player player, long proxyId) {
        return new SableProxyParticle.PosSupplier(player, proxyId);
    }

    public static RenderTransformSupplier createExpensiveProxyParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        RenderTransformSupplier baseSupplier = ProxyParticle.createRTS(player, proxyId);
        return new SableProxyParticle.RenderTransform(player, proxyId, baseSupplier);
    }

    public static Vec3Supplier createExpensiveProxyTargetParticlePosSupplier(@NotNull Player player, long proxyId) {
        return new SableProxyTargetParticle.PosSupplier(player, proxyId);
    }

    public static RenderTransformSupplier createExpensiveProxyTargetParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyTargetParticleRenderTransformSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }

        RenderTransformSupplier baseSupplier = ProxyTargetParticle.createProxyTargetParticleRenderTransformSupplier(player, proxyId);
        return new SableProxyTargetParticle.RenderTransform(player, proxyId, baseSupplier);
    }




}
