package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.CompatUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.JOMLConversion;

public class SableCompatInit {

    public static void init() {
        CompatUtil.setActiveProjection((level, pos) -> SableCompanion.INSTANCE.projectOutOfSubLevel(level, new Util.PositionImpl(pos)));

    }

}
