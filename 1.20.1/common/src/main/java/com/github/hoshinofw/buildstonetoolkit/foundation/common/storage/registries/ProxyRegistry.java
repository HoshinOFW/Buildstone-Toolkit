package com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ProxyRegistry<T extends ProxyBlockEntity<?, ?>> {

    // Section side length for the target-position skip index. Must be a power of 2 (generally 8, 16, 32).
    private static final int TARGET_INDEX_SECTION_SIZE = 16;

    private final Long2ObjectOpenHashMap<LongOpenHashSet> PosToIdMap;
    private final Long2LongOpenHashMap IdToPosMap;
    public final ProxyTargetIndex targetIndex = new ProxyTargetIndex(TARGET_INDEX_SECTION_SIZE);

    public final IdRegistry<ProxyBlockEntity<?, ?>> idRegistry;
    public final TargetIdRegistry targetIdRegistry;

    public ProxyRegistry(@NotNull IdRegistry<ProxyBlockEntity<?, ?>> idRegistry, @Nullable TargetIdRegistry targetIdRegistry) {
        PosToIdMap = new Long2ObjectOpenHashMap<>();
        IdToPosMap = new Long2LongOpenHashMap();
        this.targetIdRegistry = targetIdRegistry;
        this.idRegistry = idRegistry;
    }

    public void clear() {
        this.PosToIdMap.clear();
        this.idRegistry.clear();
        this.targetIndex.clear();
    }

    public int size() {
        return IdToPosMap.size();
    }

    public Collection<ProxyBlockEntity<?, ?>> getAllProxies() {
        return idRegistry.getAllEntries();
    }

    public Set<BlockPos> getAllPos() {
        return Util.convertToBlockPos(PosToIdMap.keySet());
    }

    public LongSet getAllLongPos() {
        return PosToIdMap.keySet();
    }

    private void addLink(long id, long pos) {
        if (id < 0) {
            //BuildstoneToolkit.LOGGER.info("Cancelled adding proxy at {} due to non-initialized id", pos);
            return;
        }
        if (targetIdRegistry != null) targetIdRegistry.internPos(pos);
        IdToPosMap.put(id, pos);
        LongOpenHashSet set = PosToIdMap.get(pos);
        if (set == null) {
            set = new LongOpenHashSet();
            PosToIdMap.put(pos, set);
            targetIndex.add(pos);
        }
        set.add(id);
    }

    private void removeLink(long id, long pos) {
        if (id < 0) {
            //BuildstoneToolkit.LOGGER.info("Cancelled removing proxy at {} due to non-initialized id", pos);
            return;
        }
        if (PosToIdMap.containsKey(pos)) {
            IdToPosMap.remove(id);
            LongSet set = PosToIdMap.get(pos);
            set.remove(id);
            if (set.isEmpty()) {
                PosToIdMap.remove(pos);
                targetIndex.remove(pos);
            }
        }
    }

    private void addLink(T proxy, BlockPos pos) {
        addLink(proxy.getId(), pos.asLong());
    }

    private void addLink(T proxy, long pos) {
        addLink(proxy.getId(), pos);
    }

    private void removeLink(T proxy, BlockPos pos) {
        removeLink(proxy.getId(), pos.asLong());
    }

    private void removeLink(T proxy, long pos) {
        removeLink(proxy.getId(), pos);
    }

    public void replaceLink(T proxy, BlockPos newPos) {
        removeProxy(proxy);
        addLink(proxy, newPos);
    }

    public void replaceLink(T proxy, long newPos) {
        removeProxy(proxy);
        addLink(proxy, newPos);
    }

    public void replaceLink(T proxy, BlockPos oldPos, BlockPos newPos) {
        removeLink(proxy, oldPos);
        addLink(proxy, newPos);
    }

    public void replaceLink(T proxy, BlockPos oldPos, long newPos) {
        removeLink(proxy, oldPos);
        addLink(proxy, newPos);
    }

    public void replaceLink(T proxy, long oldPos, BlockPos newPos) {
        removeLink(proxy, oldPos);
        addLink(proxy, newPos);
    }

    public void replaceLink(T proxy, long oldPos, long newPos) {
        removeLink(proxy, oldPos);
        addLink(proxy, newPos);
    }

    private LongSet getIdOfProxiesTargeting(BlockPos pos) {
        LongSet set = PosToIdMap.get(pos.asLong());
        return set != null ? set : LongSets.EMPTY_SET;
    }

    public LongSet getIdOfProxiesTargeting(long pos) {
        LongSet set = PosToIdMap.get(pos);
        return set != null ? set : LongSets.EMPTY_SET;
    }

    public boolean isTargeted(BlockPos pos) {
        return isTargeted(pos.asLong());
    }

    public boolean isTargeted(long pos) {
        return targetIndex.contains(pos);
    }

    public boolean isTargeting(T proxy) {
        return IdToPosMap.containsKey(proxy.getId());
    }

    /**
     * Safe under registry mutation.
     */
    public void forEachProxyTargeting(long pos, Consumer<ProxyBlockEntity<?, ?>> consumer) {
        LongSet ids = getIdOfProxiesTargeting(pos);
        int n = ids.size();
        if (n == 0) return;
        long[] snap = new long[n];
        ids.toArray(snap);
        for (long id : snap) {
            ProxyBlockEntity<?, ?> be = idRegistry.getEntry(id);
            if (be != null ) consumer.accept(be);
        }
    }

    /**
     * Safe under registry mutation.
     */
    public boolean forEachProxyTargetingUntil(long pos, Predicate<ProxyBlockEntity<?, ?>> action) {
        LongSet ids = getIdOfProxiesTargeting(pos);
        int n = ids.size();
        if (n == 0) return true;
        long[] snap = new long[n];
        ids.toArray(snap);
        for (long id : snap) {
            ProxyBlockEntity<?, ?> be = idRegistry.getEntry(id);   // lazy resolve; null-skip = correct
            if (be != null && !action.test(be)) return false;
        }
        return true;
    }

    public Collection<@NotNull ProxyBlockEntity<?, ?>> getProxiesTargeting(BlockPos pos) {
        return idRegistry.getEntries(getIdOfProxiesTargeting(pos));
    }

    public Collection<@NotNull ProxyBlockEntity<?, ?>> getProxiesTargeting(long pos) {
        return idRegistry.getEntries(getIdOfProxiesTargeting(pos));
    }

    public Collection<BlockPos> getProxyPosTargeting(BlockPos pos) {
        ArrayList<BlockPos> array = new ArrayList<>();
        getProxiesTargeting(pos).forEach((be) -> array.add(be.getBlockPos()));
        return array;
    }

    public <V extends ProxyBlockEntity<?,?>> Collection<BlockPos> getProxyPosTargeting(BlockPos pos, Class<V> beClass) {
        LongSet ids = getIdOfProxiesTargeting(pos);
        ObjectArrayList<BlockPos> out = new ObjectArrayList<>(ids.size());
        for (LongIterator it = ids.iterator(); it.hasNext(); ) {
            ProxyBlockEntity<?, ?> be = idRegistry.getEntry(it.nextLong());
            if (beClass.isInstance(be)) out.add(be.getBlockPos());
        }
        return out;
    }

    public <V extends ProxyBlockEntity<?,?>> LongCollection getProxyLongPosTargeting(BlockPos pos, Class<V> beClass) {
        return getProxyLongPosTargeting(pos.asLong(), beClass);
    }

    public <V extends ProxyBlockEntity<?,?>> LongCollection getProxyLongPosTargeting(long pos, Class<V> beClass) {
        LongSet ids = getIdOfProxiesTargeting(pos);
        LongArrayList out = new LongArrayList(ids.size());
        for (LongIterator it = ids.iterator(); it.hasNext(); ) {
            ProxyBlockEntity<?, ?> be = idRegistry.getEntry(it.nextLong());
            if (beClass.isInstance(be)) out.add(be.getBlockPos().asLong());
        }
        return out;
    }

    public void removeProxy(T proxy) {
        removeProxy(proxy.getId());
    }

    public void removeProxy(long id) {
        if (IdToPosMap.containsKey(id)) {
            long pos = IdToPosMap.get(id);
            LongSet ids = PosToIdMap.get(pos);
            if (ids != null) {
                ids.remove(id);
                if (ids.isEmpty()) {
                    PosToIdMap.remove(pos);
                    IdToPosMap.remove(id);
                    targetIndex.remove(pos);
                }
            }
        }
    }

    @Nullable
    public BlockPos getBlockTargetedBy(long id){
        ProxyBlockEntity pbe = idRegistry.getEntry(id);
        if (pbe == null) return null;
        return pbe.getLinkedAbsPos();
    }

    @Nullable
    public BlockPos getProxyPos(long id) {
        ProxyBlockEntity pbe = idRegistry.getEntry(id);
        if (pbe == null) return null;
        return pbe.getBlockPos();
    }
}
