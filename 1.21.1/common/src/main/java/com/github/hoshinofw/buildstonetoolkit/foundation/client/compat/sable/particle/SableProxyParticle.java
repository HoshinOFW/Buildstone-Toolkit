package com.github.hoshinofw.buildstonetoolkit.foundation.client.compat.sable.particle;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.ProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Util;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformContext;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SableProxyParticle {

    public record PosSupplier(Player player, long proxyId) implements Vec3Supplier {
        @Override
        public Vec3 asVec3() {
            ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbe == null) return null;
            return SableCompanion.INSTANCE.projectOutOfSubLevel(player.level(), new Util.PositionImpl(pbe.getBlockPos().getCenter()));
        }
    }

    public static final class RenderTransform implements RenderTransformSupplier {

        private final Player player;
        private final long proxyId;
        private final RenderTransformSupplier base;

        private ClientSubLevelAccess access;
        private Vec3 vec3;
        private boolean valid;

        public RenderTransform(Player player, long proxyId, RenderTransformSupplier base) {
            this.player = player;
            this.proxyId = proxyId;
            this.base = base;
            resolve();
        }

        @Override
        public void tick() {
            resolve();
        }

        private void resolve() {
            ProxyBlockEntity<?, ?> pbe = ProxyBlockEntity.getIdRegistry(player.level()).getEntry(proxyId);
            if (pbe == null) {
                valid = false;
                return;
            }
            this.access = SableCompanion.INSTANCE.getContainingClient(pbe);
            this.vec3 = pbe.getBlockPos().getCenter();
            this.valid = true;
        }

        @Override
        public RenderTransformContext get(float partialTicks) {
            if (!valid) return null;
            if (access == null) return base.get(partialTicks);
            Pose3dc pose = access.renderPose(partialTicks);
            return new RenderTransformContext(pose.transformPosition(vec3), pose.orientation());
        }
    }

}
