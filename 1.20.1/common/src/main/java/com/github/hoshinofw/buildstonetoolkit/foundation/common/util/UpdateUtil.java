package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class UpdateUtil {

    public static final ThreadLocal<int[]> DISABLE_GET_SIGNAL_WRAPPER = ThreadLocal.withInitial(() -> new int[1]);

    public static void updateProxiesTargeting(long targetPos, Level level) {
        ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(level);
        if (registry.isTargeted(targetPos)) {
            registry.forEachProxyTargeting(targetPos, (pbe) -> {
                if (pbe instanceof UpdateListenerProxyBlockEntity<?, ?> ube) {
                    if (pbe.getBlockPos().asLong() != targetPos) {
                        ube.getBlock().targetUpdated(ube, level);
                    }
                }
            });
        }
    }

    public static void targetUpdated(long targetPos, Level level) {
        ProxyBlockEntity.getRegistry(level).forEachProxyTargeting(targetPos, (pbe) -> {
            if (pbe instanceof UpdateListenerProxyBlockEntity<?, ?> ube) {
                if (pbe.getBlockPos().asLong() != targetPos) {
                    ube.getBlock().targetUpdated(ube, level);
                }
            }
        });
    }

    public static long[] packedNeighbors(BlockPos pos, boolean includeInput) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        long[] result = new long[includeInput ? 7 : 6];
        result[0] = BlockPos.asLong(x, y - 1, z); // DOWN
        result[1] = BlockPos.asLong(x, y + 1, z); // UP
        result[2] = BlockPos.asLong(x, y, z - 1); // NORTH
        result[3] = BlockPos.asLong(x, y, z + 1); // SOUTH
        result[4] = BlockPos.asLong(x - 1, y, z); // WEST
        result[5] = BlockPos.asLong(x + 1, y, z); // EAST
        if (includeInput) result[6] = BlockPos.asLong(x, y, z);
        return result;
    }

    public static long[] packedNeighborsExceptFromFacing(BlockPos pos, boolean includeInput, Direction direction) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        long[] result = new long[includeInput ? 6 : 5];
        int i = 0;
        if (direction != Direction.DOWN) result[i++] = BlockPos.asLong(x, y - 1, z); // DOWN
        if (direction != Direction.UP) result[i++] = BlockPos.asLong(x, y + 1, z); // UP
        if (direction != Direction.NORTH) result[i++] = BlockPos.asLong(x, y, z - 1); // NORTH
        if (direction != Direction.SOUTH) result[i++] = BlockPos.asLong(x, y, z + 1); // SOUTH
        if (direction != Direction.WEST) result[i++] = BlockPos.asLong(x - 1, y, z); // WEST
        if (direction != Direction.EAST) result[i++] = BlockPos.asLong(x + 1, y, z); // EAST
        if (includeInput) result[i] = BlockPos.asLong(x, y, z);
        return result;
    }

    public static int getBestTransmittedSignal(long pos, int gate, ProxyRegistry<ProxyBlockEntity<?, ?>> registry, @Nullable Direction direction, boolean isQueryingPos) {
        int best = 0;

        for (LongIterator it = registry.getIdOfProxiesTargeting(pos).iterator(); it.hasNext(); ) {
            ProxyBlockEntity<?, ?> proxy = registry.idRegistry.getEntry(it.nextLong());
            if (proxy == null) continue;
            best = foldSignal(proxy, direction, isQueryingPos, gate, best);
            if (best >= gate) return gate;
        }

        return best;
    }

    public static int getBestTransmittedSignalFromBlocks(long[] blocks, long queryingPos, int gate, ProxyRegistry<ProxyBlockEntity<?, ?>> registry) {
        int[] best = {0};

        registry.targetIndex.forEachInIfPresentUntil(blocks, (long queriedPos) -> {
            boolean querying = queriedPos == queryingPos;

            for (LongIterator it = registry.getIdOfProxiesTargeting(queriedPos).iterator(); it.hasNext(); ) {
                ProxyBlockEntity<?, ?> pbe = registry.idRegistry.getEntry(it.nextLong());
                if (pbe == null) continue;
                best[0] = foldSignal(pbe, null, querying, gate, best[0]);
                if (best[0] >= gate) return false;
            }
            return true;
        });

        return best[0];
    }

    //Alla this assumes registry is not mutated during read. In the future this may break.
    private static int foldSignal(ProxyBlockEntity<?, ?> proxy, @Nullable Direction dir,
                                  boolean isQueryingPos, int gate, int best) {
        if (proxy.getBlock() instanceof RedstoneProxyBlock rpb) {
            int transmittedSignal = rpb.transmittedSignal(proxy.getBlockState(), dir, isQueryingPos);
            if (transmittedSignal >= gate) return gate;
            if (transmittedSignal > best) return transmittedSignal;
        }
        return best;
    }

}
