package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.PistonProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.ProxyBlock;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class PistonProxyBlock extends ProxyBlock<PistonProxyBlock, PistonProxyBlockEntity> {

    @ShadowVersion
    public PistonProxyBlock(Properties properties) {
        super(properties, PistonProxyBlockEntity.class);
    }

    @ShadowVersion
    @ModifySignature("appendHoverText")
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag);


}
