package com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.ProxyParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.ProxyTargetParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.SelectionParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformContext;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SableParticleCompat {
    //TODO Add smart persist suppliers that know to return false if the block has been assembled.

    public static final boolean EXPENSIVE_MODE = true;

    public static void init() {
        if (EXPENSIVE_MODE) {
            SelectionParticle.setActiveSelectionParticlePosSupplier(SableParticleCompat::createExpensiveSelectionParticlePosSupplier);
            SelectionParticle.setActiveSelectionParticleRenderTransformSupplier(SableParticleCompat::createExpensiveSelectionParticleRenderTransformSupplier);

            ProxyParticle.setActiveProxyParticlePosSupplier(SableParticleCompat::createExpensiveProxyParticlePosSupplier);
            ProxyParticle.setActiveProxyParticleRenderTransformSupplier(SableParticleCompat::createExpensiveProxyParticleRenderTransformSupplier);

            ProxyTargetParticle.setActiveProxyTargetParticlePosSupplier(SableParticleCompat::createExpensiveProxyTargetParticlePosSupplier);
            ProxyTargetParticle.setActiveProxyTargetParticleRenderTransformSupplier(SableParticleCompat::createExpensiveProxyTargetParticleRenderTransformSupplier);
        } else {
            SelectionParticle.setActiveSelectionParticlePosSupplier(SableParticleCompat::createSelectionParticlePosSupplier);
            SelectionParticle.setActiveSelectionParticleRenderTransformSupplier(SableParticleCompat::createSelectionParticleRenderTransformSupplier);

            ProxyParticle.setActiveProxyParticlePosSupplier(SableParticleCompat::createProxyParticlePosSupplier);
            ProxyParticle.setActiveProxyParticleRenderTransformSupplier(SableParticleCompat::createProxyParticleRenderTransformSupplier);

            ProxyTargetParticle.setActiveProxyTargetParticlePosSupplier(SableParticleCompat::createProxyTargetParticlePosSupplier);
            ProxyTargetParticle.setActiveProxyTargetParticleRenderTransformSupplier(SableParticleCompat::createProxyTargetParticleRenderTransformSupplier);
        }
    }

    public static Vec3Supplier createSelectionParticlePosSupplier(@NotNull Player player, BlockPos pos) {
        if (SableCompanion.INSTANCE.isInPlotGrid(player.level(), pos)) {
            return () -> SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pos.getCenter()));
        } else {
            return SelectionParticle.createSelectionParticlePosSupplier(player, pos);
        }
    }

    public static Vec3Supplier createExpensiveSelectionParticlePosSupplier(@NotNull Player player, BlockPos pos) {
        return () -> SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pos.getCenter()));
    }

    public static RenderTransformSupplier createSelectionParticleRenderTransformSupplier(@NotNull Player player, BlockPos pos) {
        RenderTransformSupplier baseSupplier = SelectionParticle.createSelectionParticleRenderTransformSupplier(player, pos);
        ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(pos);
        if (subLevelAccess == null) {
            BuildstoneToolkit.LOGGER.warn("createSelectionParticleRenderTransformSupplier: No ClientSubLevelAccess found for position: {}", pos);
            return baseSupplier;
        }

        return (float partialTicks) ->{
            Pose3dc pose =  subLevelAccess.renderPose(partialTicks);
            return new RenderTransformContext(
                    pose.transformPosition(pos.getCenter()),
                    pose.orientation());};
    }

    public static RenderTransformSupplier createExpensiveSelectionParticleRenderTransformSupplier(@NotNull Player player, BlockPos pos) {
        RenderTransformSupplier baseSupplier = SelectionParticle.createSelectionParticleRenderTransformSupplier(player, pos);
        return (float partialTicks) ->{

            ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(pos);
            if (subLevelAccess == null) return baseSupplier.get(partialTicks);

            Pose3dc pose =  subLevelAccess.renderPose(partialTicks);
            return new RenderTransformContext(
                    pose.transformPosition(pos.getCenter()),
                    pose.orientation());};

    }

    public static Vec3Supplier createProxyParticlePosSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyParticlePosSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }
        if (SableCompanion.INSTANCE.isInPlotGrid(pbe.getLevel(), pbe.getBlockPos())) {
            return () -> {
                ProxyBlockEntity<?, ?> pbeLocal = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
                if (pbeLocal == null) return null;
                return SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pbeLocal.getBlockPos().getCenter()));
            };
        } else {
            return ProxyParticle.createProxyParticlePosSupplier(player, proxyId);
        }
    }

    public static Vec3Supplier createExpensiveProxyParticlePosSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyParticlePosSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }

        return () -> {
            ProxyBlockEntity<?, ?> pbeLocal = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbeLocal == null) return null;
            return SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pbeLocal.getBlockPos().getCenter()));
        };

    }

    public static RenderTransformSupplier createProxyParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyParticleRenderTransformSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }
        Vec3 vec3 = pbe.getBlockPos().getCenter();

        if (SableCompanion.INSTANCE.isInPlotGrid(pbe)) {
            ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(pbe);
            if (subLevelAccess == null) {
                BuildstoneToolkit.LOGGER.warn("createProxyParticleRenderTransformSupplier: No ClientSubLevelAccess found for proxy id: {}", proxyId);
                return null;
            }

            return (float partialTicks) -> {
                Pose3dc pose = subLevelAccess.renderPose(partialTicks);
                return new RenderTransformContext(
                        pose.transformPosition(vec3),
                        pose.orientation());
            };
        } else {
            return ProxyParticle.createProxyParticleRenderTransformSupplier(player, proxyId);
        }
    }

    public static RenderTransformSupplier createExpensiveProxyParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyParticleRenderTransformSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }

        RenderTransformSupplier baseSupplier = ProxyParticle.createProxyParticleRenderTransformSupplier(player, proxyId);

        return (float partialTicks) -> {
            ProxyBlockEntity<?, ?> pbeLocal = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbeLocal == null) return null;

            ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(pbeLocal);
            if (subLevelAccess == null) return baseSupplier.get(partialTicks);

            Vec3 vec3 = pbeLocal.getBlockPos().getCenter();
            Pose3dc pose = subLevelAccess.renderPose(partialTicks);
            return new RenderTransformContext(
                    pose.transformPosition(vec3),
                    pose.orientation());
        };

    }

    public static Vec3Supplier createProxyTargetParticlePosSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyTargetParticlePosSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }
        Position position = new Util.PositionImpl(pbe.getLinkedAbsPos().getCenter());
        if (SableCompanion.INSTANCE.isInPlotGrid(pbe.getLevel(), pbe.getLinkedAbsPos())) {
            return () -> SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), position);
        } else {
            return ProxyTargetParticle.createProxyTargetParticlePosSupplier(player, proxyId);
        }
    }

    public static Vec3Supplier createExpensiveProxyTargetParticlePosSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyTargetParticlePosSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }

        return () -> {
            ProxyBlockEntity<?, ?> pbeLocal = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbeLocal == null) return null;
            return SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pbeLocal.getLinkedAbsPos().getCenter()));
        };

    }

    public static RenderTransformSupplier createProxyTargetParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyTargetParticleRenderTransformSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }

        BlockPos targetPos = pbe.getLinkedAbsPos();
        if (SableCompanion.INSTANCE.isInPlotGrid(pbe.getLevel(), targetPos)) {

            ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(new Util.PositionImpl(targetPos.getCenter()));
            if (subLevelAccess == null) {
                BuildstoneToolkit.LOGGER.warn("createProxyTargetParticleRenderTransformSupplier: No ClientSubLevelAccess found for proxy id: {}", proxyId);
                return null;
            }
            return (float partialTicks) -> {
                Vec3 vec3 = pbe.getLinkedAbsPos().getCenter();
                Pose3dc pose =  subLevelAccess.renderPose(partialTicks);
                return new RenderTransformContext(
                        pose.transformPosition(vec3),
                        pose.orientation());
            };
        } else {
            return ProxyTargetParticle.createProxyTargetParticleRenderTransformSupplier(player, proxyId);
        }
    }

    public static RenderTransformSupplier createExpensiveProxyTargetParticleRenderTransformSupplier(@NotNull Player player, long proxyId) {
        ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
        if (pbe == null) {
            BuildstoneToolkit.LOGGER.warn("createProxyTargetParticleRenderTransformSupplier: No ProxyBlockEntity found for id: {}", proxyId);
            return null;
        }

        RenderTransformSupplier baseSupplier = ProxyTargetParticle.createProxyTargetParticleRenderTransformSupplier(player, proxyId);

        return (float partialTicks) -> {
            ProxyBlockEntity<?, ?> pbeLocal = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbeLocal == null) return null;

            BlockPos targetPos = pbeLocal.getLinkedAbsPos();
            ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(targetPos);
            if (subLevelAccess == null) return baseSupplier.get(partialTicks);

            Vec3 vec3 = targetPos.getCenter();
            Pose3dc pose = subLevelAccess.renderPose(partialTicks);
            return new RenderTransformContext(
                    pose.transformPosition(vec3),
                    pose.orientation());
        };

    }




}
