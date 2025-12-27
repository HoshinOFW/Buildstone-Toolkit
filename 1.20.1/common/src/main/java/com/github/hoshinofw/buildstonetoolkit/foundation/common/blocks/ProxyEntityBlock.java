package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.unstable.ProxyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ProxyEntityBlock<T extends ProxyBlockEntity<T>> extends ProxyBlock implements EntityBlock {
    public final Class<T> beclass;

    public ProxyEntityBlock(Properties properties, Class<T> BEClass) {
        super(properties);
        this.beclass = BEClass;
    }

    @Nullable
    public T getBlockEntity(Level level, BlockPos proxyPos, Class<T> beClass) {
        BlockEntity be = level.getBlockEntity(proxyPos);
        if (beClass.isInstance(be)) {
            return beClass.cast(be);
        }
        return null;
    }

    @NotNull
    public T getBlockEntity(Level level, BlockPos proxyPos) {
        return Objects.requireNonNull(getBlockEntity(level, proxyPos, this.beclass));
    }

    @Override
    public @NotNull BlockPos getLinkedAbsPos(@NotNull Level level, BlockPos proxyPos) {
        return getBlockEntity(level, proxyPos).getLinkedAbsPos();
    }

    @Override
    public @NotNull BlockPos getLinkedRelPos(@NotNull Level level, BlockPos proxyPos) {
        return getBlockEntity(level, proxyPos).getLinkedRelPos();
    }
}
