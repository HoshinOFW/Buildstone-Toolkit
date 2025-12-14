package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.RegisteredProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneProxyBlockEntity extends RegisteredProxyBlockEntity<RedstoneProxyBlockEntity> {
    private static final ProxyRegistry<RedstoneProxyBlockEntity> clientRegistry = new ProxyRegistry<>(getIdRegistry(), RedstoneProxyBlockEntity.class);

    public RedstoneProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.REDSTONE_PROXY.get(), pos, state);
    }

    @Override
    public ProxyRegistry<RedstoneProxyBlockEntity> getRegistry() {
        return clientRegistry;
    }

    public static ProxyRegistry<RedstoneProxyBlockEntity> getRegistryStatic() {
        return clientRegistry;
    }

    public int getSignal() {
        BuildstoneToolkit.LOGGER.info("getSignal called, blockState =  {}", this.getBlockState());
        return RedstoneProxyBlock.getSignal(this.getBlockState());
    }

    public RedstoneProxyBlock getBlock() {
        return (RedstoneProxyBlock) (this.getBlockState().getBlock());
    }
}
