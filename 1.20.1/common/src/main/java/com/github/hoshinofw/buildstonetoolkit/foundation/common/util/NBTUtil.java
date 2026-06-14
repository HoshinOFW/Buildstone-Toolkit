package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.storage.registries.IdObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NBTUtil {
    public static final String NBTIdKey = "buildstonetoolkit$id";
    public static final String NBTTargetIdKey = "buildstonetoolkit$target_id";
    public static final String NBTAbsTargetPosKey = "buildstonetoolkit$target";
    public static final String NBTRelTargetPosKey = "buildstonetoolkit$rel_target";
    public static final String NBTRotWatermarkKey = "buildstonetoolkit$rot_watermark";

    public static void saveAbsoluteTargetNBTFromProxy(CompoundTag nbt, ProxyBlockEntity<?, ?> proxy) {
        saveAbsPosToNBT(nbt, proxy.getTargetLongPos());
    }

    public static long getAbsoluteTargetPosFromNBT(CompoundTag nbt) {
        //Handle tag missing
        if (!nbt.contains(NBTAbsTargetPosKey, Tag.TAG_LONG)) {

            //Try defaulting to the block's position
            if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z")) {
                //BuildstoneToolkit.LOGGER.info("getAbsoluteTargetNBTFromProxy fallback to proxyPos: {}", nbt);

                return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")).asLong();
            }
            //BuildstoneToolkit.LOGGER.info("getAbsoluteTargetNBTFromProxy fallback to 0: {}", nbt);

            //Fallback to zero.
            return 0L;
        } else {
            //Normal behavior
            //BuildstoneToolkit.LOGGER.info("getAbsoluteTargetNBTFromProxy normal behavior: {}", nbt);
            return nbt.getLong(NBTAbsTargetPosKey);
        }
    }

    public static void saveRelativeTargetNBTFromProxy(CompoundTag nbt, ProxyBlockEntity<?, ?> proxy) {
        saveRelPosToNBT(nbt, proxy.getRelTargetLongPos());
    }

    public static long getRelativeTargetNBTFromPRoxy(CompoundTag nbt) {
        //Handle tag missing
        if (!nbt.contains(NBTRelTargetPosKey, Tag.TAG_LONG)) {
            BuildstoneToolkit.LOGGER.debug("getRelativeTargetNBTFromProxy fallback to 0: {}", nbt);
            //Fallback to zero, which in relative terms is the original position
            return 0L;
        } else {
            //Normal behavior
            //BuildstoneToolkit.LOGGER.info("getRelativeTargetNBTFromProxy normal behavior: {}", nbt);
            return nbt.getLong(NBTRelTargetPosKey);
        }
    }

    public static void saveRelPosToNBT(CompoundTag nbt, BlockPos pos) {
        saveAbsPosToNBT(nbt, pos);
    }

    public static void saveRelPosToNBT(CompoundTag nbt, long pos) {
        nbt.putLong(NBTRelTargetPosKey, pos);
    }

    public static void saveAbsPosToNBT(CompoundTag nbt, BlockPos pos) {
        saveAbsPosToNBT(nbt, pos.asLong());
    }

    public static void saveAbsPosToNBT(CompoundTag nbt, long pos) {
        nbt.putLong(NBTAbsTargetPosKey, pos);
    }

    public static void saveId(CompoundTag nbt, IdObject object) {
        nbt.putLong(NBTIdKey, object.getId());
    }

    public static long getId(CompoundTag nbt) {
        if (nbt.contains(NBTIdKey, Tag.TAG_LONG)) {
            return nbt.getLong(NBTIdKey);
        } else {
            return -1L;
        }
    }

    public static void saveRotWatermark(CompoundTag nbt, byte watermark) {
        nbt.putByte(NBTRotWatermarkKey, watermark);
    }

    public static byte getRotWatermark(CompoundTag nbt) {
        return nbt.contains(NBTRotWatermarkKey, Tag.TAG_BYTE)
                ? nbt.getByte(NBTRotWatermarkKey)
                : Rotation3D.IDENTITY_BYTE;
    }

    public static void saveTargetId(CompoundTag nbt, long targetId) {
        nbt.putLong(NBTTargetIdKey, targetId);
    }

    public static long getTargetId(CompoundTag nbt) {
        if (nbt.contains(NBTTargetIdKey, Tag.TAG_LONG)) {
            return nbt.getLong(NBTTargetIdKey);
        } else {
            return -1L;
        }
    }

    public static @Nullable Vec3 getSearchOriginFromNBT(CompoundTag nbt) {

        if (nbt.contains("searchOrigin", Tag.TAG_INT_ARRAY)) {
            int[] array = nbt.getIntArray("searchOrigin");
            return new BlockPos(array[0], array[1], array[2]).getCenter();
        }
        else {return null;
        }
    }

    public static void loadCustomOnly(BlockEntity be, CompoundTag tag, Level level) {
        be.load(tag);
    }

    public static CompoundTag saveWithoutId(BlockEntity be, Level level) {
        CompoundTag nbt = be.saveWithoutMetadata();
        nbt.remove(NBTIdKey);
        return nbt;
    }

    public static CompoundTag saveWithId(BlockEntity be, Level level) {
        return be.saveWithoutMetadata();
    }

    public static void relativizeTarget(CompoundTag nbt, long relLong) {
        nbt.remove(NBTUtil.NBTAbsTargetPosKey);
        NBTUtil.saveRelPosToNBT(nbt, relLong);
    }

}
