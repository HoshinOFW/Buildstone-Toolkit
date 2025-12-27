package com.github.hoshinofw.buildstonetoolkit.content.common.blocks.unstable;

import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.ObserverProxyBlockStable;
import com.github.hoshinofw.buildstonetoolkit.content.common.blocks.entity.ObserverProxyBlockEntity;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.UpdateListenerProxyBlock;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.blocks.entity.UpdateListenerProxyBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObserverProxyBlock extends ObserverProxyBlockStable {
    public ObserverProxyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("tooltip.buildstonetoolkit.observer_proxy.details"));
        } else {
            list.add(Component.translatable("tooltip.buildstonetoolkit.hold_shift"));
        }
    }
}
