package com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable.SetAllayTargetPacket;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.AllayMixinInterface;
import dev.architectury.networking.NetworkManager;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public class BuildstonePacketsRegisterAllayTargetPacket {
    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                SetAllayTargetPacket.TYPE,
                SetAllayTargetPacket.STREAM_CODEC,
                (payload, context) -> {

                    Player player = context.getPlayer();
                    if (player == null) return;

                    var level = player.level();

                    var e = level.getEntity(payload.allayId());
                    if (!(e instanceof Allay almostAllay)) return;
                    if (!(almostAllay instanceof AllayMixinInterface allay)) return;

                    Optional<UUID> optional = ((Allay)allay).getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
                    if (optional.isEmpty()
                            || !player.getUUID().equals(optional.get())
                            || (((Allay)allay).distanceToSqr(player) > 32)) {return;}

                    if (!payload.nullify()) {
                        allay.buildstonetoolkit$setSearchOrigin(payload.targetPos().getCenter());
                    } else {
                        allay.buildstonetoolkit$setSearchOrigin(null);
                    }
                });

    }

}
