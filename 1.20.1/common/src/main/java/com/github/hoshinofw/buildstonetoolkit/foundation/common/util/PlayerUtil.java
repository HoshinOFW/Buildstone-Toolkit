package com.github.hoshinofw.buildstonetoolkit.foundation.common.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PlayerMixinInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUtil {
    @Nullable
    public static BlockPos getSelectedPos(@NotNull Player player) {
        return ((PlayerMixinInterface)player).buildstonetoolkit$getSelectedBlockPos();
    }

    public static void setSelectedPos(@NotNull Player player, @Nullable BlockPos newPos) {
        ((PlayerMixinInterface)player).buildstonetoolkit$setSelectedBlockPos(newPos);
    }

    public static long getSelectedProxyId(@NotNull Player player) {
        return ((PlayerMixinInterface)player).buildstonetoolkit$getSelectedProxyId();
    }

    public static void setSelectedProxyId(@NotNull Player player, long newId) {
        ((PlayerMixinInterface)player).buildstonetoolkit$setSelectedProxyId(newId);
    }
}
