package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RightClickProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.RegisteredProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RightClickProxyBlockEntity extends RegisteredProxyBlockEntity<RightClickProxyBlockEntity> {
    public RightClickProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.RIGHT_CLICK_PROXY.get(), pos, state);
    }

    @Override
    public @NotNull ProxyRegistry<RightClickProxyBlockEntity> getRegistry() {
        return Objects.requireNonNull(RightClickProxyBlock.getBlock().getRegistry(this.getLevel()));
    }

    public static @NotNull ProxyRegistry<RightClickProxyBlockEntity> getRegistry(Level level) {
        return Objects.requireNonNull(RightClickProxyBlock.getBlock().getRegistry(level));
    }

    @Override
    public Class<RightClickProxyBlockEntity> selfClass() {
        return RightClickProxyBlockEntity.class;
    }
}
