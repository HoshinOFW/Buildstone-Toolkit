package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle.CubeParticle.CUBE;


public interface CubeParticleRenderProvider {


    default void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle) {
        Vec3 projectedView = camera.getPosition();

        float x = (float) (particle.getX() - projectedView.x());
        float y = (float) (particle.getY() - projectedView.y());
        float z = (float) (particle.getZ() - projectedView.z());

        int light = LightTexture.FULL_BRIGHT;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                Vec3 vec = CUBE[i * 4 + j].scale(-1).scale((particle.size / 2) + 0.001F).add(x, y, z);

                float u; float v;
                switch (j) {
                    case 0 -> { u = particle.maxU; v = particle.maxV; }
                    case 1 -> { u = particle.maxU; v = particle.minV; }
                    case 2 -> { u = particle.minU; v = particle.minV; }
                    default -> { u = particle.minU; v = particle.maxV; } //Case 3
                }

                consumer.vertex((float) vec.x, (float) vec.y, (float) vec.z)
                        .uv(u, v)
                        .color(particle.r, particle.g, particle.b, particle.alpha)
                        .uv2(light)
                        .endVertex();
            }
        }
    }

}
