package com.github.hoshinofw.buildstonetoolkit.foundation.client.util;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.TargetFace;
import net.minecraft.world.phys.Vec3;

public class RenderUtil {

    public static class FaceTargetRender {
        public static float width = 1f;
        public static float length = 0.2f;
        public static float height = 1f;

        public static Vec3 vec3 = new Vec3(width, height, length);

        private static final double FACE_OFFSET = 0.4;
        
        public static Vec3 getScaleForFace(TargetFace face) {
            return switch (face) {
                case UP, DOWN -> new Vec3(width, length, height);
                case NORTH, SOUTH -> new Vec3(width, height, length);
                case EAST, WEST -> new Vec3(length, width, height);
                default -> new Vec3(1, 1, 1);
            };
        }
        
        public static Vec3 getOffsetForFace(TargetFace face) {
            return switch (face) {
                case UP -> new Vec3(0, FACE_OFFSET, 0);
                case DOWN -> new Vec3(0, -FACE_OFFSET, 0);
                case SOUTH -> new Vec3(0, 0, FACE_OFFSET);
                case NORTH -> new Vec3(0, 0, -FACE_OFFSET);
                case EAST -> new Vec3(FACE_OFFSET, 0, 0);
                case WEST -> new Vec3(-FACE_OFFSET, 0, 0);
                default -> Vec3.ZERO;
            };
        }
    }

}
