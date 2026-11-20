// Modified 2026-11-15 by the hongjianjian02 fork: Simplified Chinese localization fixes.
/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.client.gui;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
final class PageFlipButton extends Button {
	private static final ResourceLocation texture = SOLSweetPotato.resourceLocation("textures/gui/food_book.png");
	public static final int width = 23;
	public static final int height = 13;

	private final Direction direction;
	private final Pageable pageable;

	PageFlipButton(int x, int y, Direction direction, Pageable pageable) {
		super(x, y, 23, 13, Component.translatable(direction == Direction.FORWARD
				? "gui.solapplepie.food_book.next_page" : "gui.solapplepie.food_book.previous_page"), (button) -> ((PageFlipButton) button).changePage(), DEFAULT_NARRATION);

		this.direction = direction;
		this.pageable = pageable;
	}

	@Override
	public void renderWidget(GuiGraphics matrices, int mouseX, int mouseY, float partialTicks) {
		if (!visible) return;

		int textureX = 0;

		boolean isHovered = getX() <= mouseX && mouseX < getX() + width && getY() <= mouseY && mouseY < getY() + height;
		if (isHovered) {
			textureX += 23;
		}

		int textureY = direction == Direction.FORWARD ? 192 : 205;

		RenderSystem.setShaderTexture(0, texture);
		matrices.blit(texture, getX(), getY(), textureX, textureY, 23, 13);
	}

	public void updateState() {
		visible = pageable.isWithinRange(pageable.getCurrentPageNumber() + direction.distance);
	}

	private void changePage() {
		pageable.switchToPage(pageable.getCurrentPageNumber() + direction.distance);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
		soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
	}

	enum Direction {
		FORWARD(1),
		BACKWARD(-1);

		final int distance;

		Direction(int distance) {
			this.distance = distance;
		}
	}

	interface Pageable {
		void switchToPage(int pageNumber);

		int getCurrentPageNumber();

		boolean isWithinRange(int pageNumber);
	}
}
