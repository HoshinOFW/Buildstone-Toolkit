package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.RedstoneProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class RedstoneProxyBlock extends UpdateListenerProxyBlock<RedstoneProxyBlock, RedstoneProxyBlockEntity> {
    @ShadowVersion
    public RedstoneProxyBlock(Properties properties) {
        super(properties, RedstoneProxyBlockEntity.class);
    }

    @ShadowVersion
    @ModifySignature("appendHoverText")
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag);
}
