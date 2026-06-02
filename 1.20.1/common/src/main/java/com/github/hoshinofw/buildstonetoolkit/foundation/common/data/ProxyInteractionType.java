package com.github.hoshinofw.buildstonetoolkit.foundation.common.data;

public enum ProxyInteractionType {

    RightClickedRightClickProxy(0),
    StoppedRightClickingRightClockProxy(1),
    LookedAtLookingAtProxy(2),
    StoppedLookingAtLookingAtProxy(3);

    private final int index;
    ProxyInteractionType(int index) {
        this.index = index;
    }

    public static ProxyInteractionType of(int index) {
        return ProxyInteractionType.values()[index];
    }

    public int getIndex() {
        return this.index;
    }
}
