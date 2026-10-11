package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

import java.util.function.LongConsumer;

public final class ProxyTargetIndex {

    //TODO Hold a private cursor here too and use it to cache the methods here too.

    private final int sectionBits;
    private final int sectionMask;
    private final int sectionWords;

    private final Long2ObjectOpenHashMap<long[]> sectionBitsets = new Long2ObjectOpenHashMap<>();

    public ProxyTargetIndex(int sectionSize) {
        this.sectionBits = Integer.numberOfTrailingZeros(sectionSize);
        this.sectionMask = sectionSize - 1;
        this.sectionWords = (sectionSize * sectionSize * sectionSize) / Long.SIZE;
    }

    public void add(long blockPos) {
        long sectionKey = sectionKey(blockPos);
        long[] bits = sectionBitsets.get(sectionKey);
        if (bits == null) {
            bits = new long[sectionWords];
            sectionBitsets.put(sectionKey, bits);
        }
        int bitIndex = bitIndex(blockPos);
        bits[bitIndex >> 6] |= 1L << (bitIndex & 63);
    }

    public void remove(long blockPos) {
        long sectionKey = sectionKey(blockPos);
        long[] bits = sectionBitsets.get(sectionKey);
        if (bits == null) return;
        int bitIndex = bitIndex(blockPos);
        bits[bitIndex >> 6] &= ~(1L << (bitIndex & 63));
        for (long w : bits) {
            if (w != 0L) return;
        }
        sectionBitsets.remove(sectionKey);
    }


    private boolean hasBit(long[] bits, long pos) {
        int bitIndex = bitIndex(pos);
        return (bits[bitIndex >> 6] & (1L << (bitIndex & 63))) != 0;
    }

    /**
     * Returns {@code true} if the given position is present in the index.
     */
    public boolean contains(long blockPos) {
        long[] bits = sectionBitsets.get(sectionKey(blockPos));
        return bits != null && hasBit(bits, blockPos);
    }

    public boolean contains(long a, long b, long c, long d, long e, long f) {
        long sk = sectionKey(a);
        long[] bits = sectionBitsets.get(sk);
        if (bits != null && hasBit(bits, a)) return true;

        long s = sectionKey(b);
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, b)) return true;

        s = sectionKey(c);
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, c)) return true;

        s = sectionKey(d);
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, d)) return true;

        s = sectionKey(e);
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, e)) return true;

        s = sectionKey(f);
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, f);
    }

    /**
     * Returns {@code true} if any of the given positions is present in the index.
     */
    public boolean contains(long... blockPos) {
        long lastSectionKey = Long.MIN_VALUE;
        long[] lastBitset = null;
        boolean lastWasMiss = false;
        for (long pos : blockPos) {
            long sectionKey = sectionKey(pos);
            if (sectionKey != lastSectionKey) {
                lastBitset = sectionBitsets.get(sectionKey);
                lastSectionKey = sectionKey;
                lastWasMiss = (lastBitset == null);
            }
            if (lastWasMiss) continue;
            assert lastBitset != null;
            if (hasBit(lastBitset, pos)) return true;
        }
        return false;
    }

    public boolean containsSelfOrNeighbors(long center) {
        int x = BlockPos.getX(center), y = BlockPos.getY(center), z = BlockPos.getZ(center);
        return contains(
                center,
                BlockPos.asLong(x, y - 1, z), BlockPos.asLong(x, y + 1, z),
                BlockPos.asLong(x, y, z - 1), BlockPos.asLong(x, y, z + 1),
                BlockPos.asLong(x - 1, y, z), BlockPos.asLong(x + 1, y, z));
    }

    public boolean containsNeighbors(long center) {
        int x = BlockPos.getX(center), y = BlockPos.getY(center), z = BlockPos.getZ(center);
        return contains(
                BlockPos.asLong(x, y - 1, z), BlockPos.asLong(x, y + 1, z),
                BlockPos.asLong(x, y, z - 1), BlockPos.asLong(x, y, z + 1),
                BlockPos.asLong(x - 1, y, z), BlockPos.asLong(x + 1, y, z));
    }

    public boolean sectionHasAny(long blockPos) {
        return sectionBitsets.containsKey(sectionKey(blockPos));
    }

    //TODO make callers own a single copy of Cursor instead of reallocating.
    public Cursor cursor() {
        return new Cursor(this);
    }

    public static final class Cursor {

        private final ProxyTargetIndex index;

        private long lastSectionKey = Long.MIN_VALUE;
        private long[] lastBitset = null;
        private boolean lastWasMiss = false;

        private Cursor(ProxyTargetIndex index) {
            this.index = index;
        }

        public boolean isPresent(long blockPos) {
            long sectionKey = index.sectionKey(blockPos);
            if (sectionKey != lastSectionKey) {
                lastBitset = index.sectionBitsets.get(sectionKey);
                lastSectionKey = sectionKey;
                lastWasMiss = (lastBitset == null);
            }
            return !lastWasMiss && index.hasBit(lastBitset, blockPos);
        }

        public void reset() {
            lastSectionKey = Long.MIN_VALUE;
            lastBitset = null;
            lastWasMiss = false;
        }
    }

    private long sectionKey(long blockPos) {
        return SectionPos.asLong(
                BlockPos.getX(blockPos) >> sectionBits,
                BlockPos.getY(blockPos) >> sectionBits,
                BlockPos.getZ(blockPos) >> sectionBits);
    }

    private int bitIndex(long blockPos) {
        int x = BlockPos.getX(blockPos);
        int y = BlockPos.getY(blockPos);
        int z = BlockPos.getZ(blockPos);
        return ((y & sectionMask) << (2 * sectionBits))
             | ((z & sectionMask) << sectionBits)
             | (x & sectionMask);
    }
}
