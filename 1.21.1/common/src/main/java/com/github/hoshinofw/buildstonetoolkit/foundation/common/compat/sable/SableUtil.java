package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.sable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.FaceTargetingProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyRegistry;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.ProxyTargetIndex;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.TargetIdRegistry;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SableUtil {

    public static void targetMovedBulk(ServerLevel originLevel, SubLevelAssemblyHelper.AssemblyTransform transform, Iterable<BlockPos> blocks) {
        ProxyRegistry<ProxyBlockEntity<?, ?>> registry = ProxyBlockEntity.getRegistry(originLevel);
        Rotation3D event = Rotation3D.fromDirectionMap(d -> rotate(transform, d));

        ProxyTargetIndex.Cursor cursor = registry.targetIndex.cursor();

        for (BlockPos oldPos : blocks) {
            long oldPosLong = oldPos.asLong();

            if (!cursor.isPresent(oldPosLong)) continue;

            BlockPos newPos = transform.apply(oldPos);

            Rotation3D regRot = Rotation3D.IDENTITY;
            long survivor = TargetIdRegistry.NO_TARGET_ID;
            long movedId = TargetIdRegistry.NO_TARGET_ID;
            if (registry.targetIdRegistry != null) {
                movedId = registry.targetIdRegistry.reverseLookup(oldPosLong);   // before retarget
                // §11 — retarget first (captures the §11.1 edge delta), then accumulate onto the live id.
                survivor = registry.targetIdRegistry.retarget(oldPosLong, newPos.asLong());
                if (TargetIdRegistry.isValidTargetId(survivor)) {
                    registry.targetIdRegistry.addRotation(survivor, event);
                    regRot = registry.targetIdRegistry.getRotation(survivor);
                }
            }

            for (ProxyBlockEntity<?, ?> pbe : registry.getProxiesTargeting(oldPos)) {
                pbe.relocateTargetPos(newPos);
                if (TargetIdRegistry.isValidTargetId(survivor) && survivor != movedId) pbe.setTargetId(survivor);
                BlockState proxyState = pbe.getBlockState();
                if (proxyState.getBlock() instanceof FaceTargetingProxyBlock ftpb) {
                    Direction faceDir = ftpb.getTargetFace(proxyState).toDirection();
                    if (faceDir != null) {
                        ftpb.setTargetFace(originLevel, pbe.getBlockPos(), proxyState,
                                TargetFace.fromDirection(rotate(transform, faceDir)));
                    }
                    if (pbe instanceof FaceTargetingProxyBlockEntity<?> ftpbe) {
                        ftpbe.setFaceRotationWatermark(regRot.toByte());
                    }
                }
            }

        }
    }

    private static Direction rotate(SubLevelAssemblyHelper.AssemblyTransform t, Direction dir) {
        BlockPos delta = t.apply(BlockPos.ZERO.relative(dir)).subtract(t.apply(BlockPos.ZERO));
        for (Direction d : Direction.values())
            if (d.getStepX() == delta.getX() && d.getStepY() == delta.getY() && d.getStepZ() == delta.getZ()) return d;
        return dir;
    }

    public static void proxyAssembled(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        if (!(newState.getBlock() instanceof ProxyBlock<?, ?>)) return;
        if (!(originLevel.getBlockEntity(oldPos) instanceof ProxyBlockEntity<?, ?> source)) return;
        if (!(resultingLevel.getBlockEntity(newPos) instanceof ProxyBlockEntity<?, ?> dest)) return;
        if (source == dest) return;
        dest.adoptIdFrom(source);
    }
}
