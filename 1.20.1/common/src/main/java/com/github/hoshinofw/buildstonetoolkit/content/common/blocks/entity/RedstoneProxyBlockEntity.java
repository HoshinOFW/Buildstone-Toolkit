package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.RedstoneProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.FaceTargetingProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.data.Rotation3D;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.registries.BuildstoneBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneProxyBlockEntity extends UpdateListenerProxyBlockEntity<RedstoneProxyBlock, RedstoneProxyBlockEntity> implements FaceTargetingProxyBlockEntity<RedstoneProxyBlock> {
    private byte rotationRegistryWatermark = Rotation3D.IDENTITY_BYTE;

    public RedstoneProxyBlockEntity(BlockPos pos, BlockState state) {
        super(BuildstoneBlockEntities.REDSTONE_PROXY.get(), pos, state);
    }

    public RedstoneProxyBlock getBlock() {
        return (RedstoneProxyBlock) (this.getBlockState().getBlock());
    }

    @Override
    public byte getFaceRotationWatermark() {
        return rotationRegistryWatermark;
    }

    @Override
    public void setFaceRotationWatermark(byte watermark) {
        this.rotationRegistryWatermark = watermark;
    }

    public int getSignal() {
        return RedstoneProxyBlock.getBlock().getSignal(this.getBlockState());
    }

    @Override
    public Class<RedstoneProxyBlockEntity> selfClass() {
        return RedstoneProxyBlockEntity.class;
    }

    @Override
    public void setRelTargetPos(long value) {
        BlockPos oldPos = this.getTargetPos();
        super.setRelTargetPos(value);
        BlockPos newPos = this.getTargetPos();
        if (level != null && !level.isClientSide() && RedstoneProxyBlock.getMode(getBlockState()) == RedstoneProxyBlock.PROXY_MODE.WRITE && isActive()) {
            level.neighborChanged(oldPos, this.getBlock(), oldPos);
            level.updateNeighborsAt(oldPos, this.getBlock());

            level.neighborChanged(newPos, this.getBlock(), newPos);
            level.updateNeighborsAt(newPos, this.getBlock());
        }
    }
}
