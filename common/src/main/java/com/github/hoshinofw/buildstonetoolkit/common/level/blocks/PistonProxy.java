package com.github.hoshinofw.buildstonetoolkit.common.level.blocks;

import com.github.hoshinofw.buildstonetoolkit.common.level.blocks.entity.PistonProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.buildstonetoolkit.util.Util;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PistonProxy extends Block implements IProxyBlock, EntityBlock, IProxyEntityBlock {

    public PistonProxy(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWER_LEVEL, 0));
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
        IProxyBlock.super.neighborChanged(state, level, pos, block, pos2, bl);
    }

    @Override
    public void onPlace(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean bl) {
        IProxyBlock.super.onPlace(oldState, level, pos, newState, bl);
    }

    @Override
    public boolean shouldPreserveTargetAbsPos(@NotNull Level level, @NotNull PistonMovingBlockEntity mbe, BlockPos originalPos, BlockPos finalPos, Direction moveDirection) {
        if (isStronglyPowered(mbe.getMovedState())) {return true;}
        return isWeaklyPowered(level, finalPos);
    }

    @Override
    public boolean shouldTransferMovement(BlockPos pos, BlockState state, Direction blockLineRecursionDirection) {
        return getPowerLevel(state) == 0;
    }

    @Override
    @Nullable
    public BlockPos getLinkedBlockPos(@NotNull Level level, BlockPos blockPos) {
        Optional<PistonProxyBlockEntity> optionalBe = level.getBlockEntity(blockPos, BuildstoneBlockEntities.PISTON_PROXY.get());
        return optionalBe.map(pistonProxyBlockEntity -> blockPos.offset(pistonProxyBlockEntity.getRelativeTargetPos())).orElse(null);
    }

    @Override
    public Util.FailableResult<BlockPos> parsePos(@NotNull Level level, BlockPos proxyPos, BlockPos inputPos) {
        BlockPos outputPos = inputPos.subtract(proxyPos);
        return new Util.FailableResult<>(outputPos, true);
    }

    @Override
    public boolean setLinkedRelPos(@NotNull Level level, BlockPos pos, BlockPos newRelativeTargetPos) {

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PistonProxyBlockEntity pbe) {
            pbe.setRelativeTargetPos(newRelativeTargetPos);
            pbe.setChanged();
            level.sendBlockUpdated(pos, pbe.getBlockState(), pbe.getBlockState(), Block.UPDATE_ALL);
            return true;
        }
        return false;
    }

    @Override
    public void offsetLinkedPos(Level level, BlockPos pos, BlockState state, Direction direction) {
        Optional<PistonProxyBlockEntity> optionalBE = level.getBlockEntity(pos, BuildstoneBlockEntities.PISTON_PROXY.get());
        if (optionalBE.isPresent()) {
            PistonProxyBlockEntity blockEntity = optionalBE.get();
            blockEntity.setRelativeTargetPos(blockEntity.getRelativeTargetPos().relative(direction.getOpposite()));
        }
    }

    @Override
    public void offsetLinkedPos(Level level, BlockPos pos, BlockState state, Direction direction, int i) {
        Optional<PistonProxyBlockEntity> optionalBE = level.getBlockEntity(pos, BuildstoneBlockEntities.PISTON_PROXY.get());
        if (optionalBE.isPresent()) {
            PistonProxyBlockEntity blockEntity = optionalBE.get();
            blockEntity.setRelativeTargetPos(blockEntity.getRelativeTargetPos().relative(direction.getOpposite(), i));
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PistonProxyBlockEntity(blockPos, blockState);
    }

    @Override
    public CompoundTag computeNewNBTWithPreservedTargetAbsPos(Level level, BlockPos pos, BlockState state, CompoundTag nbt, Direction moveDirection) {
        assert nbt != null;
        CompoundTag nbtOutput = nbt.copy();

        BlockPos newOffset = Util.getTargetPosFromNBT(nbt).relative(moveDirection.getOpposite());

        nbtOutput.putIntArray("relativeTargetPos", Util.blockPosToArray(newOffset));

        return nbtOutput;
    }

    @Override
    public BlockState computeNewStateWithPreservedTargetAbsPos(Level level, BlockPos pos, BlockState state, Direction moveDirection) {
        return state;
    }

    @Override
    public BlockState computeNewStateWithPreservedTargetAbsPos(Level level, BlockPos pos, CompoundTag nbt, BlockState state, Direction moveDirection) {
        return state;
    }
}