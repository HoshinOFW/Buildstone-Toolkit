package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ProxyEntityBlock<T extends ProxyBlockEntity> extends ProxyBlock implements EntityBlock {
    public ProxyEntityBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public T getBlockEntity(Level level, BlockPos proxyPos, Class<T> beClass) {
        BlockEntity be = level.getBlockEntity(proxyPos);
        if (beClass.isInstance(be)) {
            return beClass.cast(be);
        }
        return null;
    }
}
