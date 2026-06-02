package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class UpdateListenerProxyBlock<B extends UpdateListenerProxyBlock<B, BE>, BE extends UpdateListenerProxyBlockEntity<B, BE>> extends ProxyBlock<B, BE> implements UpdateListener{

    public UpdateListenerProxyBlock(Properties properties, Class<BE> beClass) {
        super(properties, beClass);
    }

    @Override
    public abstract void targetUpdated(UpdateListenerProxyBlockEntity<?, ?> be, @NotNull Level level);


}
