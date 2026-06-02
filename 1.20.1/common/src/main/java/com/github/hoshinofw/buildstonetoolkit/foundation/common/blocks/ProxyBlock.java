package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ProxyBlock<B extends ProxyBlock<B, BE>, BE extends ProxyBlockEntity<B, BE>> extends Block implements EntityBlock {
    public final Class<BE> beclass;

    public ProxyBlock(Properties properties, Class<BE> BEClass) {
        super(properties);
        this.beclass = BEClass;
    }

    @Override
    public void onPlace(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean bl) {
        super.onPlace(oldState, level, pos, newState, bl);
        neighborChanged(newState, level, pos, newState.getBlock(), pos, bl);
    }

    //Get linked block

    @Nullable
    public BE getProxyBlockEntity(Level level, BlockPos proxyPos, Class<BE> beClass) {
        BlockEntity be = level.getBlockEntity(proxyPos);
        if (beClass.isInstance(be)) {
            return beClass.cast(be);
        }
        return null;
    }

    public boolean hasProxyBlockEntity(Level level, BlockPos proxyPos) {
        return getProxyBlockEntity(level, proxyPos, this.beclass) != null;
    }

    @NotNull
    public BE getProxyBlockEntity(Level level, BlockPos proxyPos) {
        return Objects.requireNonNull(getProxyBlockEntity(level, proxyPos, this.beclass));
    }

    public @NotNull BlockPos getLinkedAbsPos(@NotNull Level level, BlockPos proxyPos) {
        return getProxyBlockEntity(level, proxyPos).getLinkedAbsPos();
    }

    public void setLinkedAbsPos(Level level, BlockPos proxyPos, BlockPos targetPos) {
        getProxyBlockEntity(level, proxyPos).setLinkedAbsPos(targetPos);
    }

    @Nullable
    public BlockState getLinkedBlockState(@NotNull Level level, BlockPos pos) {
        return level.getBlockState(this.getLinkedAbsPos(level, pos));
    }

    public long getId(@NotNull Level level, BlockPos proxyPos) {
        return getProxyBlockEntity(level, proxyPos).getId();
    }

}
