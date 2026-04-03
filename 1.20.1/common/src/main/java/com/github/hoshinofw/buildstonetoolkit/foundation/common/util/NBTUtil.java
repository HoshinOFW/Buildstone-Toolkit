package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.IdProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.registries.IdObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NBTUtil {
    public static final String NBTIdKey = "buildstonetoolkit$id";
    public static final String NBTTargetPosKey = "buildstonetoolkit$target";
    public static final String OLDNBTTargetPosKey = "relativeTargetPos";

    public static Long getTargetPosFromNBT(CompoundTag nbt) {
        //Handle tag missing
        if (!nbt.contains(NBTTargetPosKey, Tag.TAG_LONG)) {
            //BuildstoneToolkit.LOGGER.info("NBTUtil very very sad!");
            //Try defaulting to the old NBT
            long oldTagPos = OLDgetTargetPosFromNBT(nbt).asLong();
            if (oldTagPos != 0) {
                return oldTagPos;
            }

            //Try defaulting to the block's position
            if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z")) {
                return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")).asLong();
            }
            //Fallback to zero.
            return 0L;
        } else {
            //Normal behavior
            //BuildstoneToolkit.LOGGER.info("Everything good in NBTUtil: {}", nbt.getLong(NBTTargetPosKey));
            return nbt.getLong(NBTTargetPosKey);
        }
    }

    public static void savePosToNBT(CompoundTag nbt, BlockPos pos) {
        savePosToNBT(nbt, pos.asLong());
    }

    public static void savePosToNBT(CompoundTag nbt, long pos) {
        nbt.putLong(NBTTargetPosKey, pos);
    }

    public static void saveTargetNBTFromProxy(CompoundTag nbt, ProxyBlockEntity<?> proxy) {
        savePosToNBT(nbt, proxy.getLinkedAbsLongPos());
    }

    @Deprecated
    public static BlockPos OLDgetTargetPosFromNBT(CompoundTag nbt) {
        int[] array = nbt.getIntArray(OLDNBTTargetPosKey);

        if (!nbt.contains(OLDNBTTargetPosKey, Tag.TAG_INT_ARRAY) || (array.length < 3)) {return BlockPos.ZERO;}

        return new BlockPos(array[0], array[1], array[2]);
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

    public static void IdBlockEntityLoadLogic(IdProxyBlockEntity<?> be, CompoundTag nbt) {
        be.setId(NBTUtil.getId(nbt));
        Level level = be.getLevel();
        if (level != null) {
            IdProxyBlockEntity.getIdRegistry(level).ensureEntry(be);
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

    public static CompoundTag saveWithoutMetadata(BlockEntity be, Level level) {
        return be.saveWithoutMetadata();
    }

}
