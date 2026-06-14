package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.create;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class BuildstoneSafeNbtWriters {

    @OverwriteVersion
    @ModifySignature("safeWrite")
    public static void safeWrite(BlockEntity be, CompoundTag nbt, HolderLookup.Provider registries) {
        if (be instanceof ProxyBlockEntity<?, ?> pbe) {
            pbe.saveAdditional(nbt, registries);
        }
    }
}
