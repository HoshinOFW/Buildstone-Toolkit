package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin.compat.create;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.FaceTargetingProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.create.CreateCompatUtil;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {

    @Shadow(remap = false)
    protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;

    @Shadow(remap = false)
    protected Map<BlockPos, CompoundTag> updateTags;

    @Shadow(remap = false)
    protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Unique
    private static final String buildstonetoolkit$PARKED_TIDS_KEY = "buildstonetoolkit$parkedTargetIds";
    @Unique
    private static final String buildstonetoolkit$PARKED_KEYS_KEY = "buildstonetoolkit$parkedLocalKeys";

    @Unique
    private long[] buildstonetoolkit$parkedTargetIds = new long[0];
    @Unique
    private long[] buildstonetoolkit$parkedLocalKeys = new long[0];
    @Unique
    private final LongOpenHashSet buildstonetoolkit$capturedWorld = new LongOpenHashSet();

    @Inject(method = "writeNBT", at = @At("RETURN"), remap = false)
    private void writeParkedArrays(boolean spawnPacket, CallbackInfoReturnable<CompoundTag> cir, @Local(ordinal = 0) CompoundTag nbt) {
        if (buildstonetoolkit$parkedTargetIds.length == 0) return;
        nbt.putLongArray(buildstonetoolkit$PARKED_TIDS_KEY, buildstonetoolkit$parkedTargetIds);
        nbt.putLongArray(buildstonetoolkit$PARKED_KEYS_KEY, buildstonetoolkit$parkedLocalKeys);
    }

    @Inject(method = "readNBT", at = @At("RETURN"), remap = false)
    private void readParkedArrays(Level world, CompoundTag nbt, boolean spawnData, CallbackInfo ci) {
        buildstonetoolkit$parkedTargetIds = nbt.getLongArray(buildstonetoolkit$PARKED_TIDS_KEY);
        buildstonetoolkit$parkedLocalKeys = nbt.getLongArray(buildstonetoolkit$PARKED_KEYS_KEY);
    }

    @Inject(method = "capture", at = @At("HEAD"), remap = false)
    private void buildstonetoolkit$collectCapturedWorldPos(Level world, BlockPos pos, CallbackInfoReturnable<?> cir) {
        buildstonetoolkit$capturedWorld.add(pos.asLong());
    }

    @Inject(method = "removeBlocksFromWorld", at = @At("TAIL"), remap = false)
    private void buildstonetoolkit$parkExternalTargets(Level world, BlockPos offset, CallbackInfo ci) {
        if (buildstonetoolkit$capturedWorld.isEmpty() || !(world instanceof ServerLevel serverLevel)) {
            buildstonetoolkit$capturedWorld.clear();
            return;
        }
        CreateCompatUtil.ParkedTargets parked = CreateCompatUtil.targetsAssembled(
                serverLevel, buildstonetoolkit$capturedWorld.toLongArray(),
                w -> toLocalPos(BlockPos.of(w)).asLong());
        buildstonetoolkit$parkedTargetIds = parked.targetIds();
        buildstonetoolkit$parkedLocalKeys = parked.localKeys();
        buildstonetoolkit$capturedWorld.clear();
    }

    @Inject(method = "addBlocksToWorld",
            at = @At(value = "FIELD",
                    target = "Lcom/simibubi/create/content/contraptions/Contraption;disassembled:Z",
                    opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER),
            remap = false)
    private void buildstonetoolkit$unparkRegistry(Level world, StructureTransform transform, CallbackInfo ci) {
        if (buildstonetoolkit$parkedTargetIds.length == 0 || !(world instanceof ServerLevel serverLevel)) return;
        CreateCompatUtil.targetsDisassembledRegistry(serverLevel, transform,
                buildstonetoolkit$parkedTargetIds, buildstonetoolkit$parkedLocalKeys);
    }

    @Inject(method = "addBlocksToWorld", at = @At("TAIL"), remap = false)
    private void buildstonetoolkit$unparkProxies(Level world, StructureTransform transform, CallbackInfo ci) {
        if (buildstonetoolkit$parkedTargetIds.length == 0 || !(world instanceof ServerLevel serverLevel)) return;
        CreateCompatUtil.targetsDisassembledProxies(serverLevel, transform,
                buildstonetoolkit$parkedTargetIds, buildstonetoolkit$parkedLocalKeys);
        buildstonetoolkit$parkedTargetIds = new long[0];
        buildstonetoolkit$parkedLocalKeys = new long[0];
    }

    @Inject(method = "searchMovedStructure", at = @At("RETURN"), remap = false)
    private void buildstonetoolkit$relativizeInternalTargets(
            Level world, BlockPos pos, Direction forcedDirection,
            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry : blocks.entrySet()) {
            StructureTemplate.StructureBlockInfo info = entry.getValue();
            if (!(info.state().getBlock() instanceof ProxyBlock)) continue;
            CompoundTag nbt = info.nbt();
            if (nbt == null) continue;
            BlockPos proxyLocal = entry.getKey();
            CompoundTag updateTag = updateTags.get(proxyLocal);
            
            nbt.remove(NBTUtil.NBTTargetIdKey);
            nbt.remove(NBTUtil.NBTRotWatermarkKey);
            if (updateTag != null) {
                updateTag.remove(NBTUtil.NBTTargetIdKey);
                updateTag.remove(NBTUtil.NBTRotWatermarkKey);
            }
            
            BlockPos absTarget = BlockPos.of(NBTUtil.getAbsoluteTargetPosFromNBT(nbt));
            BlockPos targetLocal = toLocalPos(absTarget);
            
            if (!blocks.containsKey(targetLocal)) continue;
            long rel = targetLocal.subtract(proxyLocal).asLong();
            NBTUtil.relativizeTarget(nbt, rel);
            if (updateTag != null) NBTUtil.relativizeTarget(updateTag, rel);
        }
    }

    @WrapOperation(method = "addBlocksToWorld", at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;nbt()Lnet/minecraft/nbt/CompoundTag;",
                    remap = true),
            remap = false)
    private CompoundTag buildstonetoolkit$rotateProxyLink(
            StructureTemplate.StructureBlockInfo instance, Operation<CompoundTag> original,
            @Local(argsOnly = true) StructureTransform transform) {

        CompoundTag nbt = original.call(instance);
        if (nbt == null || !(instance.state().getBlock() instanceof ProxyBlock)) return nbt;
        if (!nbt.contains(NBTUtil.NBTRelTargetPosKey, CompoundTag.TAG_LONG)) return nbt;

        BlockPos proxyLocal = instance.pos();
        BlockPos targetLocal = proxyLocal.offset(BlockPos.of(nbt.getLong(NBTUtil.NBTRelTargetPosKey)));
        BlockPos rotatedRel = transform.apply(targetLocal).subtract(transform.apply(proxyLocal));
        NBTUtil.saveRelPosToNBT(nbt, rotatedRel.asLong());

        return nbt;
    }

    @WrapOperation(method = "addBlocksToWorld", at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/contraptions/StructureTransform;apply(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"),
            remap = false)
    private BlockState buildstonetoolkit$rotateProxyLinkFace(
            StructureTransform transform, BlockState state, Operation<BlockState> original,
            @Local StructureTemplate.StructureBlockInfo block) {

        BlockState result = original.call(transform, state);
        if (!(result.getBlock() instanceof FaceTargetingProxyBlock ftpb)) return result;

        CompoundTag nbt = block.nbt();
        if (nbt == null || !nbt.contains(NBTUtil.NBTRelTargetPosKey, CompoundTag.TAG_LONG)) return result;

        TargetFace face = ftpb.getTargetFace(result);
        TargetFace rotated = CreateCompatUtil.rotateFace(transform, face);
        result = ftpb.setTargetFace(result, rotated);

        return result;
    }

}
