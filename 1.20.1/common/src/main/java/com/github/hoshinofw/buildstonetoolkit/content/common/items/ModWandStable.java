package com.github.hoshinofw.buildstonetoolkit.content.common.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.Item;

public abstract class ModWandStable extends Item {

    public ModWandStable(Properties properties) {
        super(properties);
    }

    @Environment(EnvType.CLIENT)
    public enum Mode {
        OFF,
        ON
    }

    @Environment(EnvType.CLIENT)
    private static Mode CLIENT_MODE = Mode.OFF;

    @Environment(EnvType.CLIENT)
    public static Mode getClientMode() {
        return CLIENT_MODE;
    }

    @Environment(EnvType.CLIENT)
    public static void setClientMode(Mode mode) {
        CLIENT_MODE = mode;
    }

}
