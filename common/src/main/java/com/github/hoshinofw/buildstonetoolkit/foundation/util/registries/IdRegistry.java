package com.github.hoshinofw.buildstonetoolkit.foundation.util.registries;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
@MethodsReturnNonnullByDefault
public interface IdRegistry<T extends IdObject> {

    String getName();
    Long2ObjectOpenHashMap<T> getMap();

    default void ensureEntry(T entry) {
        if (entry.getId() > -1) {
            getMap().put(entry.getId(), entry);
        }
    }

    default int size() {
        return getMap().size();
    }

    default void remove(long id) {
        getMap().remove(id);
    }
    default void remove(T entry) {
        getMap().remove(entry.getId());
    }

    @Nullable
    default T getEntry(long id) {
        return getMap().get(id);
    }

    @SuppressWarnings("unchecked")
    default <V> Collection<V> getEntries(Collection<Long> ids, Class<V> entryClass) {
        ObjectOpenHashSet<V> set = new ObjectOpenHashSet<>();
        ids.forEach((id) -> {
            T entry = this.getEntry(id);
            if (entryClass.isInstance(entry)) {
                set.add((V)entry);
            }
        });
        return set;
    }

    @SuppressWarnings("unchecked")
    default <V> Collection<V> getAllEntries(Class<V> entryClass) {
        ObjectOpenHashSet<V> set = new ObjectOpenHashSet<>();
        getMap().forEach(((id, entry) -> {
            if (entryClass.isInstance(entry)) {
                set.add((V)entry);
            }
        }));
        return set;
    }

    default Collection<T> getAllEntries() {
        return getMap().values();
    }

    default void clear() {
        getMap().clear();
    }

}
