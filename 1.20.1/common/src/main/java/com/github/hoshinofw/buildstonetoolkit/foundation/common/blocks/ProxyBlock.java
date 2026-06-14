package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ProxyBlock<B extends ProxyBlock<B, BE>, BE extends ProxyBlockEntity<B, BE>> extends Block implements EntityBlock {
    public final Class<BE> beclass;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("is_active");

    public ProxyBlock(Properties properties, Class<BE> BEClass) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, true));
        this.beclass = BEClass;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    public boolean isActive(BlockState state) {
        return state.getValue(ACTIVE);
    }

    public boolean isActive(Level level, BlockPos pos) {
        return isActive(level.getBlockState(pos));
    }

    public BlockState setActive(BlockState state, boolean active) {
        return state.setValue(ACTIVE, active);
    }

    public void setActive(Level level, BlockPos pos, BlockState state, boolean active) {
        level.setBlock(pos, setActive(state, active), Block.UPDATE_CLIENTS);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean bl) {
        super.onPlace(state, level, pos, oldState, bl);
        neighborChanged(state, level, pos, state.getBlock(), pos, bl);
    }

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
        return getProxyBlockEntity(level, proxyPos).getTargetPos();
    }

    public void setLinkedAbsPos(Level level, BlockPos proxyPos, BlockPos targetPos) {
        getProxyBlockEntity(level, proxyPos).setTargetPos(targetPos);
    }

    @Nullable
    public BlockState getLinkedBlockState(@NotNull Level level, BlockPos pos) {
        return level.getBlockState(this.getLinkedAbsPos(level, pos));
    }

    public long getId(@NotNull Level level, BlockPos proxyPos) {
        return getProxyBlockEntity(level, proxyPos).getId();
    }

}
