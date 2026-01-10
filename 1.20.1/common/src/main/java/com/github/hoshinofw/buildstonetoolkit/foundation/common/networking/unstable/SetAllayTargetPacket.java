package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking.unstable;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;
import org.jetbrains.annotations.NotNull;


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

}
