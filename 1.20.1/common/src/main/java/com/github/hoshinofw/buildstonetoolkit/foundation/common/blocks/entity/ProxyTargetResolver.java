package com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.TargetIdRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.OptionalLong;

public final class ProxyTargetResolver {

    private ProxyTargetResolver() {}

    private static TargetIdRegistry targetIdRegistry(ServerLevel level) {
        return ProxyBlockEntity.getTargetIdRegistry(level);
    }

    static void relinkRegistry(ProxyBlockEntity<?, ?> pbe) {
        if (pbe.getLevel() == null) return;
        pbe.getRegistry().replaceLink(pbe, pbe.getTargetPos());
    }

    static void relinkRegistry(ProxyBlockEntity<?, ?> pbe, long newAbsPos) {
        if (pbe.getLevel() == null) return;
        pbe.getRegistry().replaceLink(pbe, newAbsPos);
    }

    static void relocate(ProxyBlockEntity<?, ?> pbe, long newAbsPos) {
        long newRel = BlockPos.of(newAbsPos).subtract(pbe.getBlockPos()).asLong();
        relinkRegistry(pbe, newAbsPos);
        if (newRel == pbe.relativeTargetPos.asLong()) return;
        long oldAbs = pbe.getTargetLongPos();
        pbe.relativeTargetPos.set(newRel);
        fireUpdates(pbe, true);
        syncActiveOnTargetChange(pbe, oldAbs, newAbsPos);
    }

    static void syncActiveOnTargetChange(ProxyBlockEntity<?, ?> pbe, long oldAbs, long newAbs) {
        boolean newOff = newAbs == TargetIdRegistry.NULL_POS;
        if ((oldAbs == TargetIdRegistry.NULL_POS) == newOff) return;
        setActiveGuarded(pbe, !newOff);
    }


    static void setActiveGuarded(ProxyBlockEntity<?, ?> pbe, boolean wantActive) {
        Level level = pbe.getLevel();
        if (level == null) return;
        BlockState state = pbe.getBlockState();
        ProxyBlock<?, ?> block = pbe.getBlock();
        if (block.isActive(state) == wantActive) return;
        block.setActive(level, pbe.getBlockPos(), state, wantActive);
    }

    static void fireUpdates(ProxyBlockEntity<?, ?> pbe, boolean notifyClient) {
        Level level = pbe.getLevel();
        if (level == null || level.isClientSide() || Util.isVirtualRenderWorld(level)) return;
        if (notifyClient) pbe.notifyUpdate();
        level.neighborChanged(pbe.getBlockPos(), pbe.getBlockState().getBlock(), pbe.getBlockPos());
    }

    static void reconcileOnSetLevel(ProxyBlockEntity<?, ?> pbe, ServerLevel serverLevel) {
        TargetIdRegistry reg = targetIdRegistry(serverLevel);
        byte edge = Rotation3D.IDENTITY_BYTE;
        if (TargetIdRegistry.isValidTargetId(pbe.targetId)) {
            long terminal = reg.resolveMigration(pbe.targetId);
            byte heldEdge = reg.migrationRotation(pbe.targetId);
            if (terminal != pbe.targetId) pbe.targetId = terminal;
            long currentAbs = pbe.getTargetLongPos();
            OptionalLong hit = reg.lookup(terminal);
            if (hit.isPresent()) {
                pbe.setTargetSilent(hit.getAsLong());
                edge = heldEdge;
            } else {
                pbe.targetId = reg.acquire(currentAbs);
                syncWatermark(pbe, serverLevel);
            }
        } else {
            claim(pbe);
        }
        applyRotationCatchUp(pbe, serverLevel, edge);
    }

    static void loadFrom(ProxyBlockEntity<?, ?> pbe, CompoundTag nbt) {
        if (pbe instanceof FaceTargetingProxyBlockEntity<?> ft) {
            ft.setFaceRotationWatermark(NBTUtil.getRotWatermark(nbt));
        }

        boolean hasTargetIdNbt = nbt.contains(NBTUtil.NBTTargetIdKey, CompoundTag.TAG_LONG);

        if (nbt.contains(NBTUtil.NBTAbsTargetPosKey, CompoundTag.TAG_LONG)) {
            pbe.setTargetSilent(NBTUtil.getAbsoluteTargetPosFromNBT(nbt));
            if (!hasTargetIdNbt) { release(pbe); claim(pbe); }
        } else if (nbt.contains(NBTUtil.NBTRelTargetPosKey, CompoundTag.TAG_LONG)) {
            pbe.setRelTargetSilent(NBTUtil.getRelativeTargetNBTFromPRoxy(nbt));
            if (!hasTargetIdNbt) { release(pbe); claim(pbe); }
        }

        if (hasTargetIdNbt) {
            long loadedTargetId = NBTUtil.getTargetId(nbt);
            pbe.targetId = loadedTargetId;

            if (TargetIdRegistry.isValidTargetId(loadedTargetId) && pbe.getLevel() instanceof ServerLevel serverLevel) {
                targetIdRegistry(serverLevel).lookup(loadedTargetId).ifPresentOrElse(
                        pbe::setTargetSilent,
                        () -> BuildstoneToolkit.LOGGER.warn("stableLoadLogic: targetId {} not found in TargetIdRegistry", loadedTargetId));
            }
        }
    }

    static void saveTo(ProxyBlockEntity<?, ?> pbe, CompoundTag nbt) {
        if (ProxyBlockEntity.storePositionAsRelative.get()) {
            NBTUtil.saveRelativeTargetNBTFromProxy(nbt, pbe);
        } else {
            NBTUtil.saveAbsoluteTargetNBTFromProxy(nbt, pbe);
        }
        if (ProxyBlockEntity.storeTargetId.get() && TargetIdRegistry.isValidTargetId(pbe.targetId)) {
            NBTUtil.saveTargetId(nbt, pbe.targetId);
            if (pbe instanceof FaceTargetingProxyBlockEntity<?> ft
                    && ft.getFaceRotationWatermark() != Rotation3D.IDENTITY_BYTE) {
                NBTUtil.saveRotWatermark(nbt, ft.getFaceRotationWatermark());
            }
        }
    }

    static void onRemoved(ProxyBlockEntity<?, ?> pbe) {
        pbe.getRegistry().removeProxy(pbe);
        release(pbe);
    }

    static void adopt(ProxyBlockEntity<?, ?> dest, ProxyBlockEntity<?, ?> src) {
        inherit(dest, src);
        relinkRegistry(dest);
    }

    private static void claim(ProxyBlockEntity<?, ?> pbe) {
        if (!(pbe.getLevel() instanceof ServerLevel serverLevel)) return;
        pbe.targetId = targetIdRegistry(serverLevel).acquire(pbe.getTargetLongPos());
        syncWatermark(pbe, serverLevel);
    }

    static void rebind(ProxyBlockEntity<?, ?> pbe, long newAbsPos) {
        if (!(pbe.getLevel() instanceof ServerLevel serverLevel)) return;
        TargetIdRegistry registry = targetIdRegistry(serverLevel);
        registry.release(pbe.targetId);
        pbe.targetId = registry.acquire(newAbsPos);
        syncWatermark(pbe, serverLevel);
    }

    private static void release(ProxyBlockEntity<?, ?> pbe) {
        if (!(pbe.getLevel() instanceof ServerLevel serverLevel)) return;
        targetIdRegistry(serverLevel).release(pbe.targetId);
        pbe.targetId = -1L;
    }

    private static void inherit(ProxyBlockEntity<?, ?> dest, ProxyBlockEntity<?, ?> src) {
        if (dest.targetId == src.targetId) return;
        if (!(dest.getLevel() instanceof ServerLevel serverLevel)) return;
        targetIdRegistry(serverLevel).release(dest.targetId);
        dest.targetId = src.targetId;
        if (dest instanceof FaceTargetingProxyBlockEntity<?> dft
                && src instanceof FaceTargetingProxyBlockEntity<?> sft) {
            dft.setFaceRotationWatermark(sft.getFaceRotationWatermark());
        }
    }

    private static void syncWatermark(ProxyBlockEntity<?, ?> pbe, ServerLevel level) {
        if (!(pbe instanceof FaceTargetingProxyBlockEntity<?> ft)) return;
        if (!TargetIdRegistry.isValidTargetId(pbe.targetId)) return;
        ft.setFaceRotationWatermark(targetIdRegistry(level).getRotation(pbe.targetId).toByte());
    }


    private static void applyRotationCatchUp(ProxyBlockEntity<?, ?> pbe, ServerLevel serverLevel, byte edge) {
        if (!(pbe instanceof FaceTargetingProxyBlockEntity<?> ft)) return;
        if (!TargetIdRegistry.isValidTargetId(pbe.targetId)) return;

        byte terminalRot = targetIdRegistry(serverLevel).getRotation(pbe.targetId).toByte();
        byte effRot = Rotation3D.compose(edge, terminalRot);
        byte watermark = ft.getFaceRotationWatermark();

        if (effRot == watermark) {
            if (edge != Rotation3D.IDENTITY_BYTE) ft.setFaceRotationWatermark(terminalRot);
            return;
        }

        BlockState state = pbe.getBlockState();
        if (!(state.getBlock() instanceof FaceTargetingProxyBlock ftpb)) return;
        TargetFace face = ftpb.getTargetFace(state);
        TargetFace newFace = Rotation3D.apply(effRot, Rotation3D.apply(Rotation3D.inverse(watermark), face));
        if (newFace != face) {
            ftpb.setTargetFaceQuietly(serverLevel, pbe.getBlockPos(), state, newFace);
        }
        ft.setFaceRotationWatermark(terminalRot);
    }
}
