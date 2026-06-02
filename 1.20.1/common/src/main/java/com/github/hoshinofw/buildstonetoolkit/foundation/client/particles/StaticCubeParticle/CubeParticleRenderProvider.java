package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformContext;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import static com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle.CUBE;


public interface CubeParticleRenderProvider {

    void preRender(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle);

    default void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle, RenderTransformSupplier transformSupplier) {
        preRender(consumer, camera, partialTicks, particle);

        Vec3 projectedView = camera.getPosition();
        RenderTransformContext ctx = transformSupplier != null ? transformSupplier.get(partialTicks) : null;
        Vec3 translation = ctx != null ? ctx.translation() : null;
        Quaterniondc rotation = ctx != null ? ctx.rotation() : null;

        double x;
        double y;
        double z;
        if (translation != null) {
             x = (translation.x() - projectedView.x());
             y = (translation.y() - projectedView.y());
             z = (translation.z() - projectedView.z());
        } else {
             x = (particle.getX() - projectedView.x());
             y = (particle.getY() - projectedView.y());
             z = (particle.getZ() - projectedView.z());
        }

        Vector3d scratch = rotation != null ? new Vector3d() : null;

        int light = LightTexture.FULL_BRIGHT;

        Vec3 half = particle.size.scale(particle.scale * 0.5).add(0.001, 0.001, 0.001);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                Vec3 c = CUBE[i * 4 + j].scale(-1).multiply(half).add(particle.renderOffset);
                double vx;
                double vy;
                double vz;
                if (rotation != null) {
                    scratch.set(c.x, c.y, c.z);
                    rotation.transform(scratch);
                    vx = scratch.x + x;
                    vy = scratch.y + y;
                    vz = scratch.z + z;
                } else {
                    vx = c.x + x;
                    vy = c.y + y;
                    vz = c.z + z;
                }

                float  u; float v;
                switch (j) {
                    case 0 -> { u = particle.maxU; v = particle.maxV; }
                    case 1 -> { u = particle.maxU; v = particle.minV; }
                    case 2 -> { u = particle.minU; v = particle.minV; }
                    default -> { u = particle.minU; v = particle.maxV; } //Case 3
                }

                buildVertex(consumer, vx, vy, vz, u, v, particle.r, particle.g, particle.b, particle.alpha, light);
            }
        }
    }

    private void buildVertex(VertexConsumer consumer, double x, double y, double z, float u, float v, float r, float g, float b, float a, int light) {
        consumer.vertex(x, y, z)
                .uv(u, v)
                .color(r, g, b, a)
                .uv2(light)
                .endVertex();
    }
}