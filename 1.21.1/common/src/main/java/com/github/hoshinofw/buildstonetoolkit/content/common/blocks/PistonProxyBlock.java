package com.github.hoshinofw.buildstonetoolkit.content.common.blocks;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.PistonProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.IdProxyBlock;
import com.github.hoshinofw.multiversion.DeleteMethodsAndFields;
import com.github.hoshinofw.multiversion.ModifySignature;
import com.github.hoshinofw.multiversion.ShadowVersion;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class PistonProxyBlock extends IdProxyBlock<PistonProxyBlockEntity> {

    @ShadowVersion
    public PistonProxyBlock(Properties properties) {
        super(properties, PistonProxyBlockEntity.class);
    }

    @ShadowVersion
    @ModifySignature("appendHoverText")
    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag);


}
