package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.quark;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public final class QuarkPistonCompat {

    private QuarkPistonCompat() {}

    public static void relinkPushedProxy(Level level, BlockPos pos, CompoundTag tag) {
        if (!(level.getBlockState(pos).getBlock() instanceof ProxyBlock)) return;
        
        tag.remove(NBTUtil.NBTTargetIdKey);
    }
}