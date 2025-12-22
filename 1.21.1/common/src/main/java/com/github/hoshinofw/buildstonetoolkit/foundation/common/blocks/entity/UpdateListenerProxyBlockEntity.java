package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class UpdateListenerProxyBlockEntity extends RegisteredProxyBlockEntity<UpdateListenerProxyBlockEntity>{
    public UpdateListenerProxyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull ProxyRegistry<UpdateListenerProxyBlockEntity> getRegistry() {
        return Objects.requireNonNull(RedstoneProxyBlock.getBlock().getRegistry(this.getLevel()));
    }

    @NotNull
    public static ProxyRegistry<UpdateListenerProxyBlockEntity> getRegistry(Level level) {
        return Objects.requireNonNull(RedstoneProxyBlock.getBlock().getRegistry(level));
    }

    public UpdateListenerProxyBlock getBlock() {
        return (UpdateListenerProxyBlock) (this.getBlockState().getBlock());
    }

    @Override
    public Class<UpdateListenerProxyBlockEntity> selfClass() {
        return UpdateListenerProxyBlockEntity.class;
    }
}
