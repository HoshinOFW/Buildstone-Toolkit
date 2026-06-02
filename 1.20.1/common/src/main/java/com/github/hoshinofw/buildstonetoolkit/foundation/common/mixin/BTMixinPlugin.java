package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BTMixinPlugin implements IMixinConfigPlugin {

    private static final boolean SABLE_PRESENT;
    static {
        boolean b;
        try {
            Class.forName("dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener", false, BTMixinPlugin.class.getClassLoader());
            b = true;
        } catch (ClassNotFoundException e) {
            b = false;
        }
        SABLE_PRESENT = b;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (isProxyBlockMixin(mixinClassName)) {
            if (SABLE_PRESENT) {
                //BuildstoneToolkit.LOGGER.info("ProxyBlockMixin applied!");
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean isProxyBlockMixin(String mixinClassName) {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
