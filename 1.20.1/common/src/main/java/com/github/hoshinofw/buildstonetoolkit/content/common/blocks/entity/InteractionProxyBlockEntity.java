package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.InteractionProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.RegisteredProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InteractionProxyBlockEntity extends RegisteredProxyBlockEntity<InteractionProxyBlockEntity> {
    public InteractionProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.INTERACTION_PROXY.get(), pos, state);
    }

    @Override
    public @NotNull ProxyRegistry<InteractionProxyBlockEntity> getRegistry() {
        return Objects.requireNonNull(InteractionProxyBlock.getBlock().getRegistry(this.getLevel()));
    }

    public static @NotNull ProxyRegistry<InteractionProxyBlockEntity> getRegistry(Level level) {
        return Objects.requireNonNull(InteractionProxyBlock.getBlock().getRegistry(level));
    }

    @Override
    public Class<InteractionProxyBlockEntity> selfClass() {
        return InteractionProxyBlockEntity.class;
    }
}
