package com.github.hoshinofw.buildstonetoolkit.foundation.util.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 Registry object to aid in efficient global proxy lookups
 */
public class ProxyRegistry<T extends IdProxyBlockEntity<?>> {
    private final Class<T> proxyBlockEntityClass;

    private final Long2ObjectOpenHashMap<LongOpenHashSet> PosToIdMap;
    private final Long2LongOpenHashMap IdToPosMap;

    private final IdRegistry<IdProxyBlockEntity<?>> idRegistry;

    public ProxyRegistry(IdRegistry<IdProxyBlockEntity<?>> idRegistry, Class<T> proxyBlockEntityClass) {
        PosToIdMap = new Long2ObjectOpenHashMap<>();
        IdToPosMap = new Long2LongOpenHashMap();
        this.idRegistry = idRegistry;
        this.proxyBlockEntityClass = proxyBlockEntityClass;
    }

    public void clear() {
        this.PosToIdMap.clear();
        this.idRegistry.clear();
    }

    public int size() {
        return idRegistry.getAllEntries(proxyBlockEntityClass).size();
    }

    public Collection<IdProxyBlockEntity<?>> getAllProxies() {
        return idRegistry.getAllEntries();
    }

    public Set<BlockPos> getAllPos() {
        return Util.convertToBlockPos(PosToIdMap.keySet());
    }

    public Set<Long> getAllLongPos() {
        return PosToIdMap.keySet();
    }

    private void addLink(long id, long pos) {
        if (id < 0) {
            throw new IndexOutOfBoundsException("Cannot add a link with id < 0");
        }
        IdToPosMap.put(id, pos);
        LongOpenHashSet set = PosToIdMap.computeIfAbsent(pos, (k) -> new LongOpenHashSet());
        set.add(id);
    }

    private void removeLink(long id, long pos) {

        if (PosToIdMap.containsKey(pos)) {
            IdToPosMap.remove(id);
            Set<Long> set = PosToIdMap.get(pos);
            set.remove(id);
            if (set.isEmpty()) {
                PosToIdMap.remove(pos);
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

    /**
     * This is a dangerous method. Make absolute sure that the proxy is still targeting the old position
     */
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

    public boolean isTargeted(BlockPos pos) {
        return isTargeted(pos.asLong());
    }

    public boolean isTargeted(long pos) {
        //BuildstoneToolkit.LOGGER.info("Targeted by proxy!");
        return PosToIdMap.containsKey(pos);
    }

    public boolean isTargeting(T proxy) {
        return IdToPosMap.containsKey(proxy.getId());
    }

    public LongSet getIdOfProxiesTargeting(BlockPos pos) {
        return new LongOpenHashSet(PosToIdMap.get(pos.asLong()));
    }

    public Collection<T> getProxiesTargeting(BlockPos pos) {
        return idRegistry.getEntries(getIdOfProxiesTargeting(pos), proxyBlockEntityClass);
    }

    public <V> Collection<V> getProxiesTargeting(BlockPos pos, Class<V> expectedClass) {
        return idRegistry.getEntries(getIdOfProxiesTargeting(pos), expectedClass);
    }

    public Collection<BlockPos> getProxiesTargetingPos(BlockPos pos) {
        ArrayList<BlockPos> array = new ArrayList<>();
        getProxiesTargeting(pos).forEach((be) -> array.add(be.getBlockPos()));
        return array;
    }

    public void removeProxy(T proxy) {
        if (IdToPosMap.containsKey(proxy.getId())) {
            long pos = IdToPosMap.get(proxy.getId());
            LongSet ids = PosToIdMap.get(pos);
            if (ids != null) {
                ids.remove(proxy.getId());
                if (ids.isEmpty()) {
                    PosToIdMap.remove(pos);
                }
            }
        }
    }
}
