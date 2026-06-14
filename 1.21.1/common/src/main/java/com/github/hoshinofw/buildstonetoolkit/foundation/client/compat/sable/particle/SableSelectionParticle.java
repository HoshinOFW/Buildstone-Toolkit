package com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable.particle;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformContext;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SableSelectionParticle {

    public record PosSupplier(Player player, BlockPos pos) implements Vec3Supplier {
        @Override
        public Vec3 asVec3() {
            return SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pos.getCenter()));
        }
    }

    public static final class RenderTransform implements RenderTransformSupplier {

        private final RenderTransformSupplier base;
        private final BlockPos pos;
        private final Vec3 vec3;

        private ClientSubLevelAccess access;

        public RenderTransform(BlockPos pos, RenderTransformSupplier base) {
            this.pos = pos;
            this.vec3 = pos.getCenter();
            this.base = base;
            resolve();
        }

        @Override
        public void tick() {
            resolve();
        }

        private void resolve() {
            this.access = SableCompanion.INSTANCE.getContainingClient(pos);
        }

        @Override
        public RenderTransformContext get(float partialTicks) {
            if (access == null) return base.get(partialTicks);
            Pose3dc pose = access.renderPose(partialTicks);
            return new RenderTransformContext(pose.transformPosition(vec3), pose.orientation());
        }
    }


}
