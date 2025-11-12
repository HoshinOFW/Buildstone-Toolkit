package com.github.hoshinofw.buildstonetoolkit.common.level.blocks;

import com.github.hoshinofw.buildstonetoolkit.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//Update everything to be blockEntity logic
public interface IProxyBlock {

    IntegerProperty POWER_LEVEL = IntegerProperty.create("power_level", 0, 2);

    default boolean isPowered(@NotNull Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }
    default boolean isPowered(BlockState state) {
        return getPowerLevel(state) != 0;
    }

    /**
     * Gets from the level, not the block's cache.
     */
    default boolean isStronglyPowered(@NotNull Level level, BlockPos pos) {
        return parseRedstoneToPowerLevel(level.getBestNeighborSignal(pos)) == 2;
    }

    default boolean isStronglyPowered(BlockState state) {
        return getPowerLevel(state) == 2;
    }

    default boolean isWeaklyPowered(@NotNull Level level, BlockPos pos) {
        return isPowered(level, pos) && !isStronglyPowered(level, pos);
    }

    default boolean isWeaklyPowered(BlockState state) {
        return isPowered(state) && !isStronglyPowered(state);
    }

    default int getPowerLevel(@NotNull Level level, BlockPos pos) {
        return getPowerLevel(level.getBlockState(pos));
    }

    default int getPowerLevel(BlockState state) {
        assert(state.getBlock() instanceof IProxyBlock);
        return state.getValue(POWER_LEVEL);
    }

    default void setPowerLevel(@NotNull Level level, BlockPos pos, int value) {
        BlockState state = level.getBlockState(pos);
        assert(state.getBlock() instanceof IProxyBlock);
        level.setBlock(pos, state.setValue(POWER_LEVEL, value), 3);
    }

    default BlockState setPowerLevel(BlockState state, int value) {
        assert(state.getBlock() instanceof IProxyBlock);
        return state.setValue(POWER_LEVEL, value);
    }

    /**
     * This parses the incoming [0, 15] redstone signal into whatever your power level cache measures. Used heavily by the default methods.
     */

    default int parseRedstoneToPowerLevel(int inputSignal) {
        if (inputSignal == 0) {return 0;}
        if (inputSignal > 7) {return 2;}
        return 1;
    }

    /**
     * This is an important method of the Block parent class. Necessary to override to cache power level internally. This default method sets your cache with setPowerLevel and parses the redstone signal with parseRedstoneToPowerLevel
     */
    default void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bl) {
        setPowerLevel(level, pos, parseRedstoneToPowerLevel(level.getBestNeighborSignal(pos)));
    }

    default void onPlace(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean bl) {
        neighborChanged(newState, level, pos, newState.getBlock(), pos, bl);
    }

    //Important proxy logic definers:
    /**
    * Called when the movement is over. MBE is going to be placed and finalized, this method defines if the placed block and its possible corresponding blockEntity have their data modified in order to preserve the absolute target position. The MBE.movedState only stores the original state of the block, so it has outdated information.
    */
    boolean shouldPreserveTargetAbsPos(@NotNull Level level, @NotNull PistonMovingBlockEntity mbe, BlockPos originalPos, BlockPos finalPos, Direction moveDirection);

    /**
     * Called before the movement actually begins, while the PistonBaseBlock class is registering different blockPos and blockState to move.
     */
    boolean shouldTransferMovement(BlockPos pos, BlockState state, Direction blockLineRecursionDirection);




    //Get linked block
    @Nullable BlockPos getLinkedBlockPos(@NotNull Level level, BlockPos pos);

    default BlockState getLinkedBlockState(@NotNull Level level, BlockPos pos) {
        return level.getBlockState(getLinkedBlockPos(level, pos));
    }
    
    //Set linked block

    /**
     * Parses position based on the proxy's logic. For example, PistonProxy can only link in 1 direction, so it returns a relative position with only one nonzero component. FreeProxy, however, simply parses to a relative BlockPos and can't fail. Should always return a valid BlockPos, but if something went wrong, set the succeeded flag to false.
     */
    Util.FailableResult<BlockPos> parsePos(@NotNull Level level, BlockPos proxyPos, BlockPos inputPos);

    /**
     * Linked block positions should be stored as offsets. This is a simpler setter meant to directly modify block data. Returns if the setting failed or succeded.
     */
    boolean setLinkedRelPos(@NotNull Level level, BlockPos pos, BlockPos newRelativeTargetPos);

    /**
     * Returns if setting the position failed or not. At the moment this is set to always true
     */
    default boolean setLinkedAbsPos(Level level, BlockPos pos, BlockPos newTargetBlockPos) {
        Util.FailableResult<BlockPos> parsedResult = parsePos(level, pos, newTargetBlockPos);
        BlockPos parsedPos = parsedResult.value();

        return setLinkedRelPos(level, pos, parsedPos) && parsedResult.succeeded();
    }

    void offsetLinkedPos(Level level, BlockPos pos, BlockState state, Direction direction);
    void offsetLinkedPos(Level level, BlockPos pos, BlockState state, Direction direction, int i);

    /**
     * This method should return a new state to be used when shouldPreserveTargetAbsPos returns True.
     */
    BlockState computeNewStateWithPreservedTargetAbsPos(Level level, BlockPos pos, BlockState state, Direction moveDirection);
}