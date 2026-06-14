package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.create;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.FaceTargetingProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyTargetIndex;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.TargetIdRegistry;
import com.simibubi.create.content.contraptions.StructureTransform;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.LongUnaryOperator;

public class CreateCompatUtil {

    public record ParkedTargets(long[] targetIds, long[] localKeys) {
        public static final ParkedTargets EMPTY = new ParkedTargets(new long[0], new long[0]);
    }

    @NotNull
    public static TargetFace rotateFace(StructureTransform transform, TargetFace face) {
        if (face == TargetFace.ALL) return TargetFace.ALL;
        return TargetFace.fromDirection(transform.rotateFacing(transform.mirrorFacing(face.toDirection())));
    }

    public static ParkedTargets targetsAssembled(ServerLevel level, long[] blocks, LongUnaryOperator worldToLocal) {
        ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(level);
        if (registry.targetIdRegistry == null) return ParkedTargets.EMPTY;

        LongArrayList tids = new LongArrayList();
        LongArrayList localKeys = new LongArrayList();
        BlockPos off = BlockPos.of(TargetIdRegistry.NULL_POS);

        ProxyTargetIndex.Cursor cursor = registry.targetIndex.cursor();

        for (long pos : blocks) {
            if (!cursor.isPresent(pos)) continue;
            long tid = registry.targetIdRegistry.reverseLookup(pos);

            if (!TargetIdRegistry.isValidTargetId(tid)) continue;

            registry.targetIdRegistry.park(tid);
            for (ProxyBlockEntity<?, ?> pbe : registry.getProxiesTargeting(pos)) {
                pbe.relocateTargetPos(off);
            }

            tids.add(tid);
            localKeys.add(worldToLocal.applyAsLong(pos));
        }

        return new ParkedTargets(tids.toLongArray(), localKeys.toLongArray());
    }

    public static void targetsDisassembledRegistry(ServerLevel level, StructureTransform transform, long[] targetIds, long[] localKeys) {
        ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(level);
        if (registry.targetIdRegistry == null) return;

        Rotation3D event = Rotation3D.fromDirectionMap(d -> transform.rotateFacing(transform.mirrorFacing(d)));
        for (int i = 0; i < targetIds.length; i++) {
            long newPos = transform.apply(BlockPos.of(localKeys[i])).asLong();
            long survivor = registry.targetIdRegistry.unpark(targetIds[i], newPos);
            if (TargetIdRegistry.isValidTargetId(survivor)) {
                registry.targetIdRegistry.addRotation(survivor, event);
            }
        }
    }

    public static void targetsDisassembledProxies(ServerLevel level, StructureTransform transform, long[] targetIds, long[] localKeys) {
        ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(level);
        if (registry.targetIdRegistry == null) return;

        Long2LongOpenHashMap newPosByTid = new Long2LongOpenHashMap(targetIds.length);
        newPosByTid.defaultReturnValue(TargetIdRegistry.NO_TARGET_ID);
        for (int i = 0; i < targetIds.length; i++) {
            newPosByTid.put(targetIds[i], transform.apply(BlockPos.of(localKeys[i])).asLong());
        }

        for (ProxyBlockEntity<?, ?> pbe : registry.getProxiesTargeting(TargetIdRegistry.NULL_POS)) {
            long heldId = pbe.getTargetId();
            long newPos = newPosByTid.get(heldId);
            if (newPos == TargetIdRegistry.NO_TARGET_ID) continue;
            long terminal = registry.targetIdRegistry.resolveMigration(heldId);
            if (terminal != heldId) pbe.setTargetId(terminal);
            pbe.relocateTargetPos(newPos);
            BlockState proxyState = pbe.getBlockState();
            if (proxyState.getBlock() instanceof FaceTargetingProxyBlock ftpb) {
                ftpb.setTargetFace(level, pbe.getBlockPos(), proxyState, rotateFace(transform, ftpb.getTargetFace(proxyState)));
                if (pbe instanceof FaceTargetingProxyBlockEntity<?> ftpbe) {
                    ftpbe.setFaceRotationWatermark(registry.targetIdRegistry.getRotation(terminal).toByte());
                }
            }
        }
    }
}
