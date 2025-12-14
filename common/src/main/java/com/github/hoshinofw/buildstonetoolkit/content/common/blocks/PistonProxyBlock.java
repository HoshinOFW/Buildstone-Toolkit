package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyEntityBlock;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.PistonProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PistonProxyBlock extends ProxyEntityBlock<PistonProxyBlockEntity> {

    public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power_level", 0, 2);

    public PistonProxyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWER_LEVEL, 0));
    }

    @Override
    public boolean isPowered(BlockState state) {
        return state.getValue(POWER_LEVEL) != 0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.piston_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER_LEVEL);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bl) {
        setPowerLevel(level, pos, parseRedstoneToPowerLevel(level.getBestNeighborSignal(pos)));
    }

    /**
     * Gets from the level, not the block's cache.
     */
    public boolean isStronglyPowered(@NotNull Level level, BlockPos pos) {
        return parseRedstoneToPowerLevel(level.getBestNeighborSignal(pos)) == 2;
    }

    public boolean isStronglyPowered(BlockState state) {
        return getPowerLevel(state) == 2;
    }

    public int getPowerLevel(BlockState state) {
        assert(state.getBlock() instanceof PistonProxyBlock);
        return state.getValue(POWER_LEVEL);
    }

    public void setPowerLevel(@NotNull Level level, BlockPos pos, int value) {
        BlockState state = level.getBlockState(pos);
        assert(state.getBlock() instanceof PistonProxyBlock);
        level.setBlock(pos, state.setValue(POWER_LEVEL, value), 3);
    }

    public BlockState setPowerLevel(BlockState state, int value) {
        assert(state.getBlock() instanceof PistonProxyBlock);
        return state.setValue(POWER_LEVEL, value);
    }

    /**
     * This parses the incoming [0, 15] redstone signal into whatever your power level cache measures. Used heavily by the public methods.
     */

    public int parseRedstoneToPowerLevel(int inputSignal) {
        if (inputSignal == 0) {return 0;}
        if (inputSignal > 7) {return 2;}
        return 1;
    }

    public boolean shouldPreserveTargetAbsPos(@NotNull Level level, @NotNull PistonMovingBlockEntity mbe, BlockPos originalPos, BlockPos finalPos, Direction moveDirection) {
        if (isStronglyPowered(mbe.getMovedState())) {return true;}
        return parseRedstoneToPowerLevel(level.getBestNeighborSignal(finalPos)) == 1;
    }


    public boolean shouldTransferMovement(BlockPos pos, BlockState state, Direction blockLineRecursionDirection) {
        return getPowerLevel(state) == 0;
    }

    @Override
    @Nullable
    public BlockPos getLinkedAbsPos(@NotNull Level level, BlockPos blockPos) {
        Optional<PistonProxyBlockEntity> optionalBe = level.getBlockEntity(blockPos, BuildstoneBlockEntities.PISTON_PROXY.get());
        if (optionalBe.isPresent()) {
            PistonProxyBlockEntity be = optionalBe.get();
            return be.getLinkedAbsPos();
        }
        return null;
    }

    @Override
    public @Nullable BlockPos getLinkedRelPos(@NotNull Level level, BlockPos pos) {
        Optional<PistonProxyBlockEntity> optionalBe = level.getBlockEntity(pos, BuildstoneBlockEntities.PISTON_PROXY.get());
        if (optionalBe.isPresent()) {
            PistonProxyBlockEntity be = optionalBe.get();
            return be.getLinkedRelPos();
        }
        return null;
    }

    @Override
    public boolean setLinkedRelPos(@NotNull Level level, BlockPos pos, BlockPos newRelativeTargetPos) {
        Optional<PistonProxyBlockEntity> optionalBe = level.getBlockEntity(pos, BuildstoneBlockEntities.PISTON_PROXY.get());
        if (optionalBe.isPresent()) {
            PistonProxyBlockEntity be = optionalBe.get();
            be.setLinkedRelPos(newRelativeTargetPos);
            return true;
        }
        return false;
    }

    @Override
    public Util.FailableResult<BlockPos> parsePos(@NotNull Level level, BlockPos proxyPos, BlockPos inputPos) {
        BlockPos outputPos = inputPos.subtract(proxyPos);
        return new Util.FailableResult<>(outputPos, true);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PistonProxyBlockEntity(blockPos, blockState);
    }

    public void saveShiftedTargetAbsPosToNBT(Level level, BlockPos pos, BlockState state, @NotNull CompoundTag nbt, Direction moveDirection) {
        Util.savePosToNBT(nbt, BlockPos.of(Util.getTargetPosFromNBT(nbt)).relative(moveDirection));
    }
}