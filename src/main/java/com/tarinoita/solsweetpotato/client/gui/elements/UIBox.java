/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.client.gui.elements;

import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class UIBox extends UIElement {
	public static UIBox horizontalLine(int minX, int maxX, int y, Color color) {
		return new UIBox(new Rectangle(minX, y, maxX + 1 - minX, 1), color);
	}

	public static UIBox verticalLine(int x, int minY, int maxY, Color color) {
		return new UIBox(new Rectangle(x, minY, 1, maxY + 1 - minY), color);
	}

	public Color color;

	public UIBox(Rectangle frame, Color color) {
		super(frame);

		this.color = color;
	}

	@Override
	protected void render(GuiGraphics matrices) {
		super.render(matrices);

		matrices.fill( frame.x, frame.y, frame.x + frame.width, frame.y + frame.height, color.getRGB());
	}
}
