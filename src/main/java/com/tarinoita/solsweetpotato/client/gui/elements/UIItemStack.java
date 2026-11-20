/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.client.gui.elements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;

import static net.minecraft.world.item.TooltipFlag.Default.ADVANCED;
import static net.minecraft.world.item.TooltipFlag.Default.NORMAL;

public class UIItemStack extends UIElement {
	public static final int size = 16;

	public ItemStack itemStack;

	public UIItemStack(ItemStack itemStack) {
		super(new Rectangle(size, size));

		this.itemStack = itemStack;
	}

	@Override
	protected void render(GuiGraphics matrices) {
		super.render(matrices);

		matrices.renderItem(
			itemStack,
			frame.x + (frame.width - size) / 2,
			frame.y + (frame.height - size) / 2
		);
	}

	@Override
	protected boolean hasTooltip() {
		return true;
	}

	@Override
	protected void renderTooltip(GuiGraphics matrices, int mouseX, int mouseY) {
		List<Component> tooltip = itemStack.getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(mc.level), mc.player, mc.options.advancedItemTooltips ? ADVANCED : NORMAL);
		renderTooltip(matrices, itemStack, tooltip, mouseX, mouseY);
	}
}
