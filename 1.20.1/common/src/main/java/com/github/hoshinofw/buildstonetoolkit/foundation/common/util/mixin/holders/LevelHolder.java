package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders;

import net.minecraft.world.level.Level;

public interface LevelHolder {

    Level buildstonetoolkit$getLevel();

    default Level getLevel() {
        return buildstonetoolkit$getLevel();
    }

}
