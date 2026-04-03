package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.AllayMixinInterface;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;


public record SetAllayTargetPacket(int allayId, BlockPos targetPos, boolean nullify) {

    public static final ResourceLocation ID =
            new ResourceLocation(BuildstoneToolkit.MOD_ID, "set_allay_target");

    public static void write(FriendlyByteBuf buf,  int allayId, @NotNull BlockPos targetPos, boolean nullify) {
        buf.writeInt(allayId);
        buf.writeBlockPos(targetPos);
        buf.writeBoolean(nullify);
    }

    public static SetAllayTargetPacket read(FriendlyByteBuf buf) {
        int allayId = buf.readInt();
        BlockPos targetPos = buf.readBlockPos();
        boolean nullify = buf.readBoolean();
        return new SetAllayTargetPacket(allayId, targetPos, nullify);
    }

    public static void sendToServer(int allayId, @NotNull BlockPos targetPos, boolean nullify) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf, allayId, targetPos, nullify);
        NetworkManager.sendToServer(ID, buf);
    }

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                ID,
                (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
                    SetAllayTargetPacket payload = read(buf);

                    context.queue(() -> {
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
        );
    }
}
