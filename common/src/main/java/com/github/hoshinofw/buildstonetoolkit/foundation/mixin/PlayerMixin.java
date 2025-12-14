package com.github.hoshinofw.buildstonetoolkit.foundation.mixin;

import com.github.hoshinofw.buildstonetoolkit.foundation.util.mixin.PlayerMixinInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public class PlayerMixin implements PlayerMixinInterface {
    @Unique
    private BlockPos buildstonetoolkit$selectedBlockPos = null;

    @Unique
    public BlockPos buildstonetoolkit$getSelectedBlockPos() {
        return this.buildstonetoolkit$selectedBlockPos;
    }

    @Unique
    public void buildstonetoolkit$setSelectedBlockPos(BlockPos pos) {
        this.buildstonetoolkit$selectedBlockPos = pos;
    }

}
