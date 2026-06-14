package com.github.hoshinofw.buildstonetoolkit.foundation.common.data;

import net.minecraft.core.Direction;

import java.util.function.UnaryOperator;

public final class Rotation3D {

    public static final byte IDENTITY_BYTE = (byte) (2 * 6 + 1);

    //To avoid callers doing math and allocations, build big lookup tables
    private static final Rotation3D[] BY_BYTE = new Rotation3D[36];
    private static final TargetFace[] TARGET_FACES = TargetFace.values();
    private static final byte[] APPLY_DIR = new byte[36 * 6];
    private static final byte[] COMPOSE = new byte[36 * 36];
    private static final byte[] INVERSE = new byte[36];

    public static final Rotation3D IDENTITY;

    static {
        Rotation3D[] interned = new Rotation3D[36];
        for (int b = 0; b < 36; b++) {
            Direction n = Direction.from3DDataValue(b / 6);
            Direction u = Direction.from3DDataValue(b % 6);
            if (n.getAxis() != u.getAxis()) interned[b] = new Rotation3D(n, u);
        }
        IDENTITY = interned[IDENTITY_BYTE & 0xFF];
        for (int b = 0; b < 36; b++) {
            BY_BYTE[b] = interned[b] != null ? interned[b] : IDENTITY;
        }

        for (int b = 0; b < 36; b++) {
            int north3d = b / 6, up3d = b % 6;
            boolean degenerate = Direction.from3DDataValue(north3d).getAxis()
                    == Direction.from3DDataValue(up3d).getAxis();
            for (int d = 0; d < 6; d++) {
                APPLY_DIR[b * 6 + d] = (byte) (degenerate ? d : applyDir(north3d, up3d, d));
            }
        }

        for (int a = 0; a < 36; a++) {
            for (int b = 0; b < 36; b++) {
                int north = APPLY_DIR[b * 6 + (APPLY_DIR[a * 6 + 2] & 0xFF)] & 0xFF;
                int up = APPLY_DIR[b * 6 + (APPLY_DIR[a * 6 + 1] & 0xFF)] & 0xFF;
                COMPOSE[a * 36 + b] = (byte) (north * 6 + up);
            }
        }

        for (int b = 0; b < 36; b++) {
            int invNorth = 2, invUp = 1;
            for (int d = 0; d < 6; d++) {
                int img = APPLY_DIR[b * 6 + d] & 0xFF;
                if (img == 2) invNorth = d;
                if (img == 1) invUp = d;
            }
            INVERSE[b] = (byte) (invNorth * 6 + invUp);
        }
    }

    public final Direction north;
    public final Direction up;
    private final byte packed;

    private Rotation3D(Direction north, Direction up) {
        this.north = north;
        this.up = up;
        this.packed = (byte) (north.get3DDataValue() * 6 + up.get3DDataValue());
    }


    public static Rotation3D of(Direction north, Direction up) {
        if (north == null || up == null || north.getAxis() == up.getAxis()) return IDENTITY;
        return BY_BYTE[north.get3DDataValue() * 6 + up.get3DDataValue()];
    }

    public static Rotation3D fromByte(byte b) {
        int i = b & 0xFF;
        return i < 36 ? BY_BYTE[i] : IDENTITY;
    }

    public static Rotation3D fromDirectionMap(UnaryOperator<Direction> rotate) {
        return of(rotate.apply(Direction.NORTH), rotate.apply(Direction.UP));
    }

    public byte toByte() {
        return packed;
    }

    public Direction apply(Direction d) {
        return Direction.from3DDataValue(APPLY_DIR[(packed & 0xFF) * 6 + d.get3DDataValue()]);
    }

    public TargetFace apply(TargetFace face) {
        return apply(packed, face);
    }

    public Rotation3D compose(Rotation3D next) {
        return fromByte(compose(packed, next));
    }

    public Rotation3D inverse() {
        return BY_BYTE[INVERSE[packed & 0xFF] & 0xFF];
    }

    public static TargetFace apply(byte rot, TargetFace face) {
        if (face == TargetFace.ALL) return TargetFace.ALL;
        return TARGET_FACES[APPLY_DIR[(rot & 0xFF) * 6 + face.getIndex()] & 0xFF];
    }

    public static Direction apply(byte rot, Direction d) {
        return Direction.from3DDataValue(APPLY_DIR[(rot & 0xFF) * 6 + d.get3DDataValue()]);
    }

    public static byte compose(byte first, Rotation3D next) {
        return compose(first, next.packed);
    }

    public static byte compose(byte first, byte second) {
        return COMPOSE[(first & 0xFF) * 36 + (second & 0xFF)];
    }

    public static byte inverse(byte rot) {
        return INVERSE[rot & 0xFF];
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Rotation3D r && r.packed == packed;
    }

    @Override
    public int hashCode() {
        return packed;
    }

    @Override
    public String toString() {
        return "Rotation3D[north=" + north + ", up=" + up + "]";
    }

    private static int applyDir(int north3d, int up3d, int d3d) {
        return switch (d3d) {
            case 2 -> north3d;
            case 3 -> north3d ^ 1;
            case 1 -> up3d;
            case 0 -> up3d ^ 1;
            case 5 -> cross3d(north3d, up3d);
            case 4 -> cross3d(north3d, up3d) ^ 1;
            default -> d3d;
        };
    }

    private static int cross3d(int a3d, int b3d) {
        Direction a = Direction.from3DDataValue(a3d);
        Direction b = Direction.from3DDataValue(b3d);
        int x = a.getStepY() * b.getStepZ() - a.getStepZ() * b.getStepY();
        int y = a.getStepZ() * b.getStepX() - a.getStepX() * b.getStepZ();
        int z = a.getStepX() * b.getStepY() - a.getStepY() * b.getStepX();
        for (int d = 0; d < 6; d++) {
            Direction dir = Direction.from3DDataValue(d);
            if (dir.getStepX() == x && dir.getStepY() == y && dir.getStepZ() == z) return d;
        }
        return 5;
    }
}