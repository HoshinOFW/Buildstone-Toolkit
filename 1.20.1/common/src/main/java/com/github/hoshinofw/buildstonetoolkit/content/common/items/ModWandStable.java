package com.github.hoshinofw.buildstonetoolkit.content.common.items;


import net.minecraft.world.item.Item;

public abstract class ModWandStable extends Item {

    public ModWandStable(Properties properties) {
        super(properties);
    }

    public enum Mode {
        OFF,
        ON
    }

    private static Mode CLIENT_MODE = Mode.OFF;

    public static Mode getClientMode() {
        return CLIENT_MODE;
    }

    public static void setClientMode(Mode mode) {
        CLIENT_MODE = mode;
    }

}
