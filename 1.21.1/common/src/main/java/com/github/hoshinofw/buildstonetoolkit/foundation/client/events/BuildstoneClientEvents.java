package com.github.hoshinofw.buildstonetoolkit.foundation.client.events;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import net.minecraft.client.Minecraft;

@ModifyClass
public class BuildstoneClientEvents {

    @OverwriteVersion
    private static void registerMouseScrolled() {
        ClientRawInputEvent.MOUSE_SCROLLED.register(BuildstoneClientEvents::mouseScrolled);
    }

    @ShadowVersion
    private static EventResult mouseScrolled(Minecraft minecraft, double x, double y);
}
