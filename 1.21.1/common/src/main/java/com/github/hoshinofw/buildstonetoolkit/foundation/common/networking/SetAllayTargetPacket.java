package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.AllayMixinInterface;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.OverwriteVersion;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

@DeleteMethodsAndFields({"read", "write"})
public record SetAllayTargetPacket(int allayId, BlockPos targetPos, boolean nullify) implements CustomPacketPayload{

    @OverwriteVersion
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(BuildstoneToolkit.MOD_ID, "set_allay_target");

    public static final CustomPacketPayload.Type<SetAllayTargetPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetAllayTargetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SetAllayTargetPacket::allayId,
                    BlockPos.STREAM_CODEC, SetAllayTargetPacket::targetPos,
                    ByteBufCodecs.BOOL, SetAllayTargetPacket::nullify,
                    SetAllayTargetPacket::new
            );

    @OverwriteVersion
    public static void sendToServer(int allayId, BlockPos targetPos, boolean nullify) {
        NetworkManager.sendToServer(new SetAllayTargetPacket(allayId, targetPos, nullify));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OverwriteVersion
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
