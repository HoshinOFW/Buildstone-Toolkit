package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.LookingAtProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.RegisteredProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LookingAtProxyBlockEntity extends RegisteredProxyBlockEntity<LookingAtProxyBlockEntity> {
    public LookingAtProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.LOOKING_AT_PROXY.get(), pos, state);
    }

    @Override
    public @NotNull ProxyRegistry<LookingAtProxyBlockEntity> getRegistry() {
        return Objects.requireNonNull(LookingAtProxyBlock.getBlock().getRegistry(this.getLevel()));
    }

    public static @NotNull ProxyRegistry<LookingAtProxyBlockEntity> getRegistry(Level level) {
        return Objects.requireNonNull(LookingAtProxyBlock.getBlock().getRegistry(level));
    }

    @Override
    public Class<LookingAtProxyBlockEntity> selfClass() {
        return LookingAtProxyBlockEntity.class;
    }
}
