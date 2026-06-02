package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.sable;


import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable.ProxyMoveListener;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ProxyBlock.class)
public abstract class ProxyBlockMixin extends Block implements ProxyMoveListener {
    public ProxyBlockMixin(Properties properties) {
        super(properties);
    }
}
