package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render;

public enum BlockParticleTexture {

    SELECTION_BLOCK(0),
    PROXY_BLOCK(1),
    PROXY_TARGET_BLOCK(2),
    REDSTONE_PROXY_TRANSITION(3);

    private final int index;

    BlockParticleTexture(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

}
