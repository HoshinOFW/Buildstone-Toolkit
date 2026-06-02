package com.github.hoshinofw.buildstonetoolkit.foundation.common.data;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TargetFace implements StringRepresentable {
    DOWN(0, "down"),
    UP(1, "up"),
    NORTH(2, "north"),
    SOUTH(3, "south"),
    WEST(4, "west"),
    EAST(5, "east"),
    ALL(6, "none"),
    ;

    private final int index;
    private final String name;

    TargetFace(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public static TargetFace fromDirection(Direction direction) {
        if (direction == null) return ALL;
        return TargetFace.values()[direction.get3DDataValue()];
    }

    @Nullable
    public Direction toDirection() {
        if (this == ALL) return null;
        return Direction.values()[index];
    }

    public static TargetFace fromIndex(int index) {
        return TargetFace.values()[index];
    }
}
