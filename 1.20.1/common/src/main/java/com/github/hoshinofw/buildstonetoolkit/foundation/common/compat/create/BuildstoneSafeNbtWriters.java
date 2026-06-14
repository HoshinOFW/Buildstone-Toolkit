package com.github.hoshinofw.buildstonetoolkit.foundation.common.compat.create;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class BuildstoneSafeNbtWriters {

    public static void register() {
        for (Supplier<? extends BlockEntityType<? extends ProxyBlockEntity<?, ?>>> beType : BuildstoneBlockEntities.getProxyTypes()) {
            SafeNbtWriterRegistry.REGISTRY.register(beType.get(), BuildstoneSafeNbtWriters::safeWrite);
        }
    }

    public static void safeWrite(BlockEntity be, CompoundTag nbt) {
        if (be instanceof ProxyBlockEntity<?, ?> pbe) {
            pbe.saveAdditional(nbt);
        }
    }

}
