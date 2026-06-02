package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.VisionProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.InteractiveProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class VisionProxyBlock extends ProxyBlock<VisionProxyBlock, VisionProxyBlockEntity> implements InteractiveProxyBlock {

    @ShadowVersion
    public VisionProxyBlock(Properties properties) {
        super(properties, VisionProxyBlockEntity.class);
    }

    @ShadowVersion
    @ModifySignature("appendHoverText")
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag);
}
