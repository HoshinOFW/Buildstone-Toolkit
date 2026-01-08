package com.github.hoshinofw.buildstonetoolkit.foundation.common.mixin;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.mixin.PlayerMixinInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements PlayerMixinInterface {
    @Unique
    private BlockPos buildstonetoolkit$selectedBlockPos = null;

    @Unique
    private long buildstonetoolkit$selectedProxyId = -1;

    @Unique
    public BlockPos buildstonetoolkit$getSelectedBlockPos() {
        return this.buildstonetoolkit$selectedBlockPos;
    }

    @Unique
    public void buildstonetoolkit$setSelectedBlockPos(BlockPos pos) {
        this.buildstonetoolkit$selectedBlockPos = pos;
    }

    @Override
    public long buildstonetoolkit$getSelectedProxyId() {
        return this.buildstonetoolkit$selectedProxyId;
    }

    @Override
    public void buildstonetoolkit$setSelectedProxyId(long pos) {
        this.buildstonetoolkit$selectedProxyId = pos;
    }

}
