package com.github.hoshinofw.buildstonetoolkit.foundation.util.registries;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
@MethodsReturnNonnullByDefault
public class IdRegistry<T extends IdObject> {
    private long nextId = 0;
    private final Long2ObjectOpenHashMap<T> map = new Long2ObjectOpenHashMap<>();

    private long allocateID() {
        long output = nextId;
        this.nextId++;
        return output;
    }

    public void addEntry(T entry) {
        if (entry.getId() > -1) {
            map.put(entry.getId(), entry);
        }
    }

    /**
     * Returns id to be stored in entry and retrieved with the idObject#getId method.
     */
    public long register(T entry) {
        long id = allocateID();
        map.put(id, entry);
        return id;
    }

    public int size() {
        return map.size();
    }

    public void remove(long id) {
        map.remove(id);
    }
    public void remove(T entry) {
        map.remove(entry.getId(), entry);
    }

    @Nullable
    public T getEntry(long id) {
        return map.get(id);
    }

    @SuppressWarnings("unchecked")
    public <V> Collection<V> getEntries(Collection<Long> ids, Class<V> entryClass) {
        ObjectOpenHashSet<V> set = new ObjectOpenHashSet<>();
        ids.forEach((id) -> {
            T entry = this.getEntry(id);
            if (entryClass.isInstance(entry)) {
                set.add((V)entry);
            }
        });
        return set;
    }

    public Collection<T> getAllEntries() {
        return map.values();
    }

}
