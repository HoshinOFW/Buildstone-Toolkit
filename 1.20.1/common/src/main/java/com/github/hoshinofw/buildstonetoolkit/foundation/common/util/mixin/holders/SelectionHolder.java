package com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.holders;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectionHolder {
    @Nullable BlockPos buildstonetoolkit$getSelectedBlockPos();
    void buildstonetoolkit$setSelectedBlockPos(@Nullable BlockPos pos);

    long buildstonetoolkit$getSelectedProxyId();
    void buildstonetoolkit$setSelectedProxyId(long id);

    @NotNull TargetFace buildstonetoolkit$getSelectedTargetFace();
    void buildstonetoolkit$setSelectedTargetFace(@NotNull TargetFace face);

    default @Nullable BlockPos getSelectedPos() {
        return buildstonetoolkit$getSelectedBlockPos();
    }

    default long getSelectedId() {
        return buildstonetoolkit$getSelectedProxyId();
    }

    default @NotNull TargetFace getSelectedFace() {
        return buildstonetoolkit$getSelectedTargetFace();
    }

    default boolean hasSelectedPos() {
        return buildstonetoolkit$getSelectedBlockPos() != null;
    }

    default boolean hasSelectedProxyId() {
        return buildstonetoolkit$getSelectedProxyId() >= 0;
    }

    default boolean hasSelectedFace() {
        return buildstonetoolkit$getSelectedTargetFace() != TargetFace.ALL;
    }

    default void clearSelection() {
        buildstonetoolkit$setSelectedProxyId(-1);
        buildstonetoolkit$setSelectedBlockPos(null);
        buildstonetoolkit$setSelectedTargetFace(TargetFace.ALL);
    }

    default void setSelectedId(long id) {
        buildstonetoolkit$setSelectedProxyId(id);
        buildstonetoolkit$setSelectedBlockPos(null);
        buildstonetoolkit$setSelectedTargetFace(TargetFace.ALL);
    }

    default void setSelectedPos(BlockPos pos) {
        buildstonetoolkit$setSelectedProxyId(-1);
        buildstonetoolkit$setSelectedBlockPos(pos);
        buildstonetoolkit$setSelectedTargetFace(TargetFace.ALL);
    }

    default void setSelectedId(long id, TargetFace face) {
        buildstonetoolkit$setSelectedProxyId(id);
        buildstonetoolkit$setSelectedBlockPos(null);
        buildstonetoolkit$setSelectedTargetFace(face);
    }

    default void setSelectedPos(BlockPos pos, TargetFace face) {
        buildstonetoolkit$setSelectedProxyId(-1);
        buildstonetoolkit$setSelectedBlockPos(pos);
        buildstonetoolkit$setSelectedTargetFace(face);
    }

}
