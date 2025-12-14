package com.github.hoshinofw.buildstonetoolkit.foundation.util.registries;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.util.Util;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 Registry object to aid in efficient global proxy lookups
 */
public class ProxyRegistry<T extends IdProxyBlockEntity<T>> {
    private final Class<T> expectedClass;

    private final Long2LongOpenHashMap IdToPosMap = new Long2LongOpenHashMap();
    private final Long2ObjectOpenHashMap<LongSet> PosToIdMap = new Long2ObjectOpenHashMap<>();

    private final IdRegistry<IdProxyBlockEntity<?>> idRegistry;

    public ProxyRegistry(IdRegistry<IdProxyBlockEntity<?>> idRegistry, Class<T> expectedClass) {
        this.idRegistry = idRegistry;
        this.expectedClass = expectedClass;
    }

    public int size() {
        return IdToPosMap.size();
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

    public void addLink(long id, long pos) {
        PosToIdMap.computeIfAbsent(pos, (k) -> LongOpenHashSet.of(id));
        IdToPosMap.putIfAbsent(id, pos);
    }

    public void removeLink(long id, long pos) {
        IdToPosMap.remove(id);
        if (PosToIdMap.containsKey(pos)) {
            Set<Long> set = PosToIdMap.get(pos);
            set.remove(id);
            if (set.isEmpty()) {
                PosToIdMap.remove(pos);
            }
        }
    }

    public void addLink(T proxy, BlockPos pos) {
        addLink(proxy.getId(), pos.asLong());
    }

    public void addLink(T proxy, long pos) {
        addLink(proxy.getId(), pos);
    }

    public void removeLink(T proxy, BlockPos pos) {
        removeLink(proxy.getId(), pos.asLong());
    }

    public void removeLink(T proxy, long pos) {
        removeLink(proxy.getId(), pos);
    }

    public void replaceLink(T proxy, BlockPos oldPos, BlockPos newPos) {
        removeLink(proxy, oldPos);
        addLink(proxy, newPos);
    }

    public void replaceLink(T proxy, BlockPos newPos) {
        Long oldPos = getLongTargetOf(proxy);
        if (oldPos != null) {
            removeLink(proxy, oldPos);
            addLink(proxy, newPos);
        } else {
            addLink(proxy, newPos);
        }
    }

    public void replaceLink(T proxy, long newPos) {
        Long oldPos = getLongTargetOf(proxy);
        if (oldPos != null) {
            removeLink(proxy, oldPos);
            addLink(proxy, newPos);
        } else {
            addLink(proxy, newPos);
        }
    }

    public boolean isTargeted(BlockPos pos) {
        return isTargeted(pos.asLong());
    }

    public boolean isTargeted(long pos) {
        return PosToIdMap.containsKey(pos);
    }

    public LongSet getIdOfProxiesTargeting(BlockPos pos) {
        LongSet proxySet = PosToIdMap.get(pos.asLong());
        if (proxySet == null) {return LongSet.of();}
        return proxySet;
    }

    public Collection<T> getProxiesTargeting(BlockPos pos) {
        Collection<T> collection = idRegistry.getEntries(getIdOfProxiesTargeting(pos), expectedClass);
        Set<T> set = new HashSet<>();

        for (T entry : collection) {
            if (expectedClass.isInstance(entry)) {
                set.add(entry);
            }
        }

        return set;
    }

    @Nullable
    public BlockPos getBlockPosTargetOf(T proxy) {
        Long i = getLongTargetOf(proxy);
        if (i == null) {return null;}
        return BlockPos.of(i);
    }

    @Nullable
    public Long getLongTargetOf(T proxy) {
        return IdToPosMap.get(proxy.getId());
    }

    public void removeProxy(T proxy) {
        long pos = IdToPosMap.remove(proxy.getId());
        LongSet ids = PosToIdMap.get(pos);
        if (ids != null) {
            ids.remove(proxy.getId());
            if (ids.isEmpty()) {
                PosToIdMap.remove(pos);
            }
        }
    }
}
