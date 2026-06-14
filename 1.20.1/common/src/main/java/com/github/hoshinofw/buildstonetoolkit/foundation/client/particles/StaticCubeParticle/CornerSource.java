package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

public interface CornerSource {

    void prepareCorners(CubeParticle particle);

    final class Static implements CornerSource {
        static final Static INSTANCE = new Static();

        @Override
        public void prepareCorners(CubeParticle particle) {

        }
    }

    final class Dynamic implements CornerSource {
        static final Dynamic INSTANCE = new Dynamic();

        @Override
        public void prepareCorners(CubeParticle particle) {
            if (particle.cornersDirty) {
                particle.recomputeCorners();
                particle.cornersDirty = false;
            }
        }
    }
}
