package com.github.hoshinofw.buildstonetoolkit.foundation.common.networking;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.ProxyInteractionType;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public record PlayerProxyInteractionPacket(long[] proxyPos, Vec3 playerPosition, ProxyInteractionType[] interactionTypes) {

    public static final ResourceLocation ID = BuildstoneToolkit.RLFromPath("proxy_interaction");

    public static void write(FriendlyByteBuf buf, long[] proxyPos, @NotNull Vec3 playerPosition, ProxyInteractionType[] interactionTypes) {
        buf.writeLongArray(proxyPos);
        buf.writeVector3f(playerPosition.toVector3f());
        int[] array = new int[interactionTypes.length];
        int index = 0;
        for (ProxyInteractionType type : (interactionTypes)) {
            array[index] = type.getIndex();
            index++;
        }
        buf.writeVarIntArray(array);
    }

    public static PlayerProxyInteractionPacket read(FriendlyByteBuf buf) {
        long[] proxyPos = buf.readLongArray();
        Vec3 playerPosition = new Vec3(buf.readVector3f());
        int[] array = buf.readVarIntArray();
        int index = 0;
        ProxyInteractionType[] iTypeArray = new ProxyInteractionType[array.length];
        for (int typeIndex : array) {
            iTypeArray[index] = ProxyInteractionType.of(typeIndex);
            index++;
        }
        return new PlayerProxyInteractionPacket(proxyPos, playerPosition, iTypeArray);
    }

    public static void sendToServer(long[] proxyPositions, @NotNull Vec3 playerPosition, ProxyInteractionType interactionType) {
        sendToServer(proxyPositions, playerPosition, new ProxyInteractionType[]{interactionType});
    }

    public static void sendToServer(long[] proxyPositions, @NotNull Vec3 playerPosition, ProxyInteractionType[] interactionTypes) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf, proxyPositions, playerPosition, interactionTypes);
        if (interactionTypes.length > 1) {
            int i = 1;
        }
        NetworkManager.sendToServer(ID, buf);
    }

    public static void register() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                ID,
                (FriendlyByteBuf buf, NetworkManager.PacketContext context) -> {
                    PlayerProxyInteractionPacket payload = read(buf);
                    context.queue(() -> handlePacket(payload, context));
                });
    }

    private static void handlePacket(PlayerProxyInteractionPacket payload, NetworkManager.PacketContext context) {
        if (!(context.getPlayer() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        for (long l : payload.proxyPos()) {
            BlockPos proxyPos = BlockPos.of(l);
            BlockState proxyState = serverLevel.getBlockState(proxyPos);
            if (proxyState.getBlock() instanceof InteractiveProxyBlock interactiveProxyBlock) {
                interactiveProxyBlock.handleInteraction(serverLevel, proxyPos, proxyState, context.getPlayer().position(), payload.interactionTypes());
            }
        }
    }
}
