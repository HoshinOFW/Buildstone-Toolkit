package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable.VisionProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.RegisteredProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.registries.ProxyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VisionProxyBlockEntity extends RegisteredProxyBlockEntity<VisionProxyBlockEntity> {
    public VisionProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.VISION_PROXY.get(), pos, state);
    }

    @Override
    public @NotNull ProxyRegistry<VisionProxyBlockEntity> getRegistry() {
        return Objects.requireNonNull(VisionProxyBlock.getBlock().getRegistry(this.getLevel()));
    }

    public static @NotNull ProxyRegistry<VisionProxyBlockEntity> getRegistry(Level level) {
        return Objects.requireNonNull(VisionProxyBlock.getBlock().getRegistry(level));
    }

    @Override
    public Class<VisionProxyBlockEntity> selfClass() {
        return VisionProxyBlockEntity.class;
    }
}
