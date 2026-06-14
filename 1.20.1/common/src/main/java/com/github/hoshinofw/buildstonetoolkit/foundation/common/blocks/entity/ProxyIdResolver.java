package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.ProxyIdStorage;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ServerIdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;


public final class ProxyIdResolver {

    private ProxyIdResolver() {}

    private static ServerIdRegistry<ProxyBlockEntity<?, ?>> reg(ServerLevel level) {
        return ProxyIdStorage.getServerIdRegistry(level);
    }

    static void saveTo(ProxyBlockEntity<?, ?> pbe, CompoundTag nbt) {
        NBTUtil.saveId(nbt, pbe);
    }

    static void reconcileOnSetLevel(ProxyBlockEntity<?, ?> pbe, ServerLevel serverLevel) {
        ServerIdRegistry<ProxyBlockEntity<?, ?>> idRegistry = reg(serverLevel);

        if (!ProxyBlockEntity.isValidId(pbe.id)) {
            pbe.id = idRegistry.registerNew(pbe);
        } else {
            // If valid, check if a new id must be assigned.
            if (idRegistry.hasEntry(pbe.id)) {
                ProxyBlockEntity<?, ?> existingBe = idRegistry.getEntry(pbe.id);
                if (existingBe == null) {
                    throw new RuntimeException("I hope this doesn't happen part 1");
                } else if (existingBe == pbe || existingBe.isRemoved() || serverLevel.getBlockEntity(existingBe.getBlockPos()) != existingBe) {
                    idRegistry.ensureEntry(pbe);
                } else {
                    long oldId = pbe.id;
                    pbe.id = idRegistry.registerNew(pbe);
                    pbe.getRegistry().removeProxy(oldId);
                    idRegistry.recycleAndEnsure(oldId, pbe);
                }
            } else {
                idRegistry.ensureEntry(pbe);
            }
        }
    }

    static void reconcileClientOnSetLevel(ProxyBlockEntity<?, ?> pbe) {
        if (ProxyBlockEntity.isValidId(pbe.id)) {
            ProxyIdStorage.getClientRegistry().ensureEntry(pbe);
        }
    }

    static void loadFrom(ProxyBlockEntity<?, ?> pbe, CompoundTag nbt) {
        Level level = pbe.getLevel();
        long newId = NBTUtil.getId(nbt);

        if (level == null) {
            // World is still initializing, set id.
            pbe.id = newId;
            pbe.setChanged();
            return;
        }

        if (level.isClientSide()) {
            loadClient(pbe, newId);
        } else if (level instanceof ServerLevel serverLevel) {
            loadServer(pbe, serverLevel, newId);
        }
    }

    private static void loadClient(ProxyBlockEntity<?, ?> pbe, long newId) {
        pbe.id = newId;
        IdRegistry<ProxyBlockEntity<?, ?>> clientRegistry = ProxyIdStorage.getClientRegistry();
        if (ProxyBlockEntity.isValidId(newId) && clientRegistry.getEntry(pbe.id) != pbe) {
            clientRegistry.ensureEntry(pbe);
        }
    }

    private static void loadServer(ProxyBlockEntity<?, ?> pbe, ServerLevel serverLevel, long newId) {
        ServerIdRegistry<ProxyBlockEntity<?, ?>> registry = reg(serverLevel);

        if (ProxyBlockEntity.isValidId(newId)) {
            if (!registry.hasEntry(newId)) {
                // Use newId
                long oldId = pbe.id;
                if (ProxyBlockEntity.isValidId(oldId)) {
                    pbe.id = newId;
                    pbe.getRegistry().removeProxy(oldId);
                    registry.recycleAndEnsure(oldId, pbe);
                } else {
                    pbe.id = newId;
                    registry.ensureEntry(pbe);
                }
                pbe.notifyUpdate();
            } else {
                ProxyBlockEntity<?, ?> existingBe = registry.getEntry(newId);
                if (existingBe == null) {
                    throw new RuntimeException("I hope this doesn't happen part 2");
                } else if (existingBe == pbe) {
                    registry.ensureEntry(pbe);
                } else if (existingBe.isRemoved() || serverLevel.getBlockEntity(existingBe.getBlockPos()) != existingBe) {
                    // Take the new id if existingBe in the registry is removed or no longer in the world.
                    pbe.id = newId;
                    registry.ensureEntry(pbe);
                    pbe.notifyUpdate();
                }
            }
        } else {
            // Keep oldId
            registry.ensureEntry(pbe);
        }
    }

    static void onRemoved(ProxyBlockEntity<?, ?> pbe) {
        ProxyBlockEntity.getIdRegistry(pbe.getLevel()).remove(pbe);
    }

    static void adopt(ProxyBlockEntity<?, ?> dest, ProxyBlockEntity<?, ?> src) {
        if (!(dest.getLevel() instanceof ServerLevel serverLevel)) return;
        ServerIdRegistry<ProxyBlockEntity<?, ?>> registry = reg(serverLevel);
        long placeholderId = dest.id;

        dest.id = src.id;
        if (ProxyBlockEntity.isValidId(placeholderId) && placeholderId != src.id) {
            dest.getRegistry().removeProxy(placeholderId);
            registry.recycleAndEnsure(placeholderId, dest);
        } else {
            registry.ensureEntry(dest);
        }
    }
}