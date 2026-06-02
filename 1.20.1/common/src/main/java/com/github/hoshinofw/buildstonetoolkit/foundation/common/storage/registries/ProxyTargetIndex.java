package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

import java.util.function.LongConsumer;

//TODO Cursor which holds a small cache which allows other parts of the codebase to efficiently walk without being forced to pass lambdas to this class.
public final class ProxyTargetIndex {

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

    /**
     * Tests whether {@code pos}'s bit is set within an already-resolved section bitset.
     * Shared by every contains/walk path so the bit-index math lives in one place. Caller
     * guarantees {@code bits != null}.
     */
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

    // Fixed-arity contains(...) overloads (2..8). They avoid the long[] that the varargs
    // contains(long...) allocates at every call site, while keeping the same section-cached walk:
    // the bitset is only re-fetched when a position falls in a different section than the previous
    // one, so the common case (all positions in one section) costs a single map lookup. Each
    // short-circuits on the first hit.
    public boolean contains(long a, long b) {
        long sk = sectionKey(a);
        long[] bits = sectionBitsets.get(sk);
        if (bits != null && hasBit(bits, a)) return true;

        long s = sectionKey(b);
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, b);
    }

    public boolean contains(long a, long b, long c) {
        long sk = sectionKey(a);
        long[] bits = sectionBitsets.get(sk);
        if (bits != null && hasBit(bits, a)) return true;

        long s = sectionKey(b);
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, b)) return true;

        s = sectionKey(c);
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, c);
    }

    public boolean contains(long a, long b, long c, long d) {
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
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, d);
    }

    public boolean contains(long a, long b, long c, long d, long e) {
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
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, e);
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

    public boolean contains(long a, long b, long c, long d, long e, long f, long g) {
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
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, f)) return true;

        s = sectionKey(g);
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, g);
    }

    public boolean contains(long a, long b, long c, long d, long e, long f, long g, long h) {
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
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, f)) return true;

        s = sectionKey(g);
        if (s != sk) { bits = sectionBitsets.get(s); sk = s; }
        if (bits != null && hasBit(bits, g)) return true;

        s = sectionKey(h);
        if (s != sk) bits = sectionBitsets.get(s);
        return bits != null && hasBit(bits, h);
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

    public void clear() {
        sectionBitsets.clear();
    }

    public void forEachInIfPresent(Iterable<BlockPos> blocks, LongConsumer consumer) {
        long lastSectionKey = Long.MIN_VALUE;
        long[] lastBitset = null;
        boolean lastWasMiss = false;
        for (BlockPos block : blocks) {
            long pos = block.asLong();
            long sectionKey = sectionKey(pos);
            if (sectionKey != lastSectionKey) {
                lastBitset = sectionBitsets.get(sectionKey);
                lastSectionKey = sectionKey;
                lastWasMiss = (lastBitset == null);
            }
            if (lastWasMiss) continue;
            assert lastBitset != null;
            if (hasBit(lastBitset, pos)) consumer.accept(pos);
        }
    }

    /**
     * Efficiently walks only the blockPos in {@code blocks} that are found in the index, invoking {@code consumer}.
     */
    public void forEachInIfPresent(long[] blocks, LongConsumer consumer) {
        long lastSectionKey = Long.MIN_VALUE;
        long[] lastBitset = null;
        boolean lastWasMiss = false;
        for (long pos : blocks) {
            long sectionKey = sectionKey(pos);
            if (sectionKey != lastSectionKey) {
                lastBitset = sectionBitsets.get(sectionKey);
                lastSectionKey = sectionKey;
                lastWasMiss = (lastBitset == null);
            }
            if (lastWasMiss) continue;
            assert lastBitset != null;
            if (hasBit(lastBitset, pos)) consumer.accept(pos);
        }
    }

    /**
     * Efficiently walks only the blockPos in {@code blocks} that are found in the index, until {@code consumer} returns false.
     */
    public boolean forEachInIfPresentUntil(long[] blocks, LongPredicate consumer) {
        long lastSectionKey = Long.MIN_VALUE;
        long[] lastBitset = null;
        boolean lastWasMiss = false;
        for (long pos : blocks) {
            long sectionKey = sectionKey(pos);
            if (sectionKey != lastSectionKey) {
                lastBitset = sectionBitsets.get(sectionKey);
                lastSectionKey = sectionKey;
                lastWasMiss = (lastBitset == null);
            }
            if (lastWasMiss) continue;
            assert lastBitset != null;
            if (hasBit(lastBitset, pos) && !consumer.test(pos)) return false;
        }
        return true;
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
