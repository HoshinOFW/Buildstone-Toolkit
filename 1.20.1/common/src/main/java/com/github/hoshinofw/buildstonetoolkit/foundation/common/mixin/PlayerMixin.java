package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders.SelectionHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements SelectionHolder {
    @Unique
    private BlockPos buildstonetoolkit$selectedBlockPos = null;

    @Unique
    private long buildstonetoolkit$selectedProxyId = -1;

    @Unique @NotNull
    TargetFace buildstonetoolkit$selectedTargetFace = TargetFace.ALL;

    @Unique
    public BlockPos buildstonetoolkit$getSelectedBlockPos() {
        return this.buildstonetoolkit$selectedBlockPos;
    }

    @Unique
    public void buildstonetoolkit$setSelectedBlockPos(@Nullable BlockPos pos) {
        this.buildstonetoolkit$selectedBlockPos = pos;
    }

    @Override
    public long buildstonetoolkit$getSelectedProxyId() {
        return this.buildstonetoolkit$selectedProxyId;
    }

    @Override
    public void buildstonetoolkit$setSelectedProxyId(long pos) {
        this.buildstonetoolkit$selectedProxyId = pos;
    }

    @Override
    public @NotNull TargetFace buildstonetoolkit$getSelectedTargetFace() {
        return buildstonetoolkit$selectedTargetFace;
    }

    @Override
    public void buildstonetoolkit$setSelectedTargetFace(@NotNull TargetFace face) {
        this.buildstonetoolkit$selectedTargetFace = face;
    }

}
