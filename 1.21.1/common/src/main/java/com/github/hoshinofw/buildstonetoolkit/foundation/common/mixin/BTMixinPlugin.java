package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin;

import com.github.hoshinofw.multiversion.ModifyClass;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.github.hoshinofw.multiversion.ShadowVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@ModifyClass
public class BTMixinPlugin implements IMixinConfigPlugin {

    @OverwriteVersion
    public static boolean isProxyBlockMixin(String mixinClassName) {
        return Objects.equals(mixinClassName,"com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.sable.ProxyBlockMixin");
    }

    @ShadowVersion
    @Override
    public void onLoad(String mixinPackage);

    @ShadowVersion
    @Override
    public String getRefMapperConfig();

    @ShadowVersion
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName);

    @ShadowVersion
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets);

    @ShadowVersion
    @Override
    public List<String> getMixins();

    @ShadowVersion
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo);

    @ShadowVersion
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo);
}
