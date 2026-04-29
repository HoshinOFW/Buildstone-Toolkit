package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.ponder;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.minecraft.network.chat.Component;

public abstract class PonderScenes {

    public static void observerProxy(SceneBuilder builder, SceneBuildingUtil util) {
        PonderSceneBuilder scene = new PonderSceneBuilder(builder.getScene());
        scene.title("observer_proxy_1", Component.translatable("").toString());

    }

}
