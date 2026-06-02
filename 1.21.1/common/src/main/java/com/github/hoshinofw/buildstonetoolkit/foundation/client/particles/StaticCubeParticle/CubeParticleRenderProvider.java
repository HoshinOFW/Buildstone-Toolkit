package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.mojang.blaze3d.vertex.VertexConsumer;


public interface CubeParticleRenderProvider {

    @OverwriteVersion
    private void buildVertex(VertexConsumer consumer, double x, double y, double z, float u, float v, float r, float g, float b, float a, int light) {
        consumer.addVertex((float) x, (float) y, (float) z)
                .setUv(u, v)
                .setColor(r, g, b, a)
                .setLight(light);
    }
}
