package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.BlockParticleTexture;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformContext;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import java.util.concurrent.ThreadLocalRandom;

public class RPTransitionParticle {
    private static final float SHAKE_TRANSLATE_AMP = 0.04F;
    private static final float SHAKE_ROTATE_AMP = 0.12F;
    private static final float SHAKE_GAUSSIAN_SIGMA = 0.18F;

    public static void spawn(Player player, BlockPos proxyPos, long proxyId) {
        if (player.level() instanceof ClientLevel clientLevel) {
            Particle particle = CubeParticle.create(clientLevel, proxyPos)
                    .setRGBATint(1, 1, 1, 1F)
                    .setSize(1.07F)
                    .setFadeDuration(2)
                    .setCull(true)
                    .setTextureIndex(BlockParticleTexture.REDSTONE_PROXY_TRANSITION)
                    .setRenderTransformContextSupplier(createRPTransformRTS(player, proxyPos, proxyId,4, 3))
                    .build();
            //BuildstoneToolkit.LOGGER.info("Summoned Redstone Transition Particle: {} at: {}", particle, proxyPos);
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    private static final class ShakeState {
        long lastJitterIndex = 0L;
        Vec3 prevTrans = Vec3.ZERO;
        Vec3 nextTrans = randomUnitVec3();
        Quaterniond prevRot = new Quaterniond();
        Quaterniond nextRot = randomShakeRotation();
    }

    private static Vec3 randomUnitVec3() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double x = r.nextGaussian();
        double y = r.nextGaussian();
        double z = r.nextGaussian();
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1.0e-9) return new Vec3(1, 0, 0);
        return new Vec3(x / len, y / len, z / len);
    }

    private static Quaterniond randomShakeRotation() {
        Vec3 axis = randomUnitVec3();
        double half = SHAKE_ROTATE_AMP * 0.5;
        double s = Math.sin(half);
        return new Quaterniond(axis.x * s, axis.y * s, axis.z * s, Math.cos(half));
    }

    public static RenderTransformSupplier createRPTransformRTS(@NotNull Player player, @NotNull BlockPos proxyPos, long proxyId, int duration, float jittersPerTick) {
        RenderTransformSupplier regularRTS = ProxyParticle.activeProxyParticleRenderTransformSupplier.apply(player, proxyId);

        long startTick = player.level().getGameTime();
        float period = 1F / jittersPerTick;
        Vec3 basePos = proxyPos.getCenter();
        ShakeState s = new ShakeState();

        return (float partialTicks) -> {
            long now = player.level().getGameTime();
            float elapsedTicks = (now - startTick) + partialTicks;
            float progress = Mth.clamp(elapsedTicks / (float) duration, 0F, 1F);

            float jitterPos = elapsedTicks / period;
            long jitterIndex = (long) Math.floor(jitterPos);
            float withinPeriod = jitterPos - jitterIndex;

            long steps = jitterIndex - s.lastJitterIndex;
            for (long i = 0; i < steps; i++) {
                s.prevTrans = s.nextTrans;
                s.prevRot = new Quaterniond(s.nextRot);
                s.nextTrans = randomUnitVec3();
                s.nextRot = randomShakeRotation();
            }
            s.lastJitterIndex = jitterIndex;

            float env = (float) Math.exp(-Math.pow((progress - 0.5F) / SHAKE_GAUSSIAN_SIGMA, 2));

            Vec3 shakeOffset = s.prevTrans
                    .lerp(s.nextTrans, withinPeriod)
                    .scale(SHAKE_TRANSLATE_AMP * env);

            Quaterniond rot = new Quaterniond(s.prevRot).slerp(s.nextRot, withinPeriod);
            Quaterniond shakeRot = new Quaterniond().slerp(rot, env);
            
            RenderTransformContext regularCtx = regularRTS.get(partialTicks);
            if (regularCtx == null) {
                return new RenderTransformContext(basePos.add(shakeOffset), shakeRot);
            }
            
            Vec3 tReg = regularCtx.translation();
            Quaterniondc rReg = regularCtx.rotation();
            Vec3 base = tReg != null ? tReg : basePos;

            if (rReg == null) {
                return new RenderTransformContext(base.add(shakeOffset), shakeRot);
            }

            Vector3d rotatedOffset = rReg.transform(new Vector3d(shakeOffset.x, shakeOffset.y, shakeOffset.z));
            Vec3 compositeTranslation = base.add(rotatedOffset.x, rotatedOffset.y, rotatedOffset.z);
            Quaterniond compositeRotation = new Quaterniond(rReg).mul(shakeRot);

            return new RenderTransformContext(compositeTranslation, compositeRotation);
        };
    }
}
