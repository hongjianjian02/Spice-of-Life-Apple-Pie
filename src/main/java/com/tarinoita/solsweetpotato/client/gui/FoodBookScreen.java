// Modified 2026-11-15 by the hongjianjian02 fork: Simplified Chinese localization fixes.
/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Modified 2026-11-20: build all food book pages from one history summary.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.client.gui;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.SOLSweetPotatoConfig;
import com.tarinoita.solsweetpotato.client.gui.elements.*;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import com.tarinoita.solsweetpotato.tracking.benefits.BenefitInfo;
import com.tarinoita.solsweetpotato.tracking.benefits.BenefitsHandler;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.tarinoita.solsweetpotato.lib.Localization.localized;

@OnlyIn(Dist.CLIENT)
public final class FoodBookScreen extends Screen implements PageFlipButton.Pageable {
	private static final ResourceLocation texture = SOLSweetPotato.resourceLocation("textures/gui/food_book.png");
	private static final UIImage.Image bookImage = new UIImage.Image(texture, new Rectangle(0, 0, 186, 192));
	static final UIImage.Image carrotImage = new UIImage.Image(texture, new Rectangle(0, 240, 16, 16));

	static final Color fullBlack = Color.BLACK;
	static final Color lessBlack = new Color(0, 0, 0, 128);
	static final Color leastBlack = new Color(0, 0, 0, 64);
	static final Color activeGreen = new Color(29, 104, 29, 255);
	static final Color inactiveRed = new Color(104, 29, 29, 255);

	private final List<UIElement> elements = new ArrayList<>();
	private UIImage background;
	private UILabel pageNumberLabel;

	private PageFlipButton nextPageButton;
	private PageFlipButton prevPageButton;

	private final Player player;

	private final List<Page> pages = new ArrayList<>();
	private int currentPageNumber = 0;

	public static void open(Player player) {
		Minecraft.getInstance().setScreen(new FoodBookScreen(player));
	}

	public FoodBookScreen(Player player) {
		super(Component.translatable("item.solapplepie.food_book"));
		this.player = player;
	}

	@Override
	public void init() {
		super.init();

		background = new UIImage(bookImage);
		background.setCenterX(width / 2);
		background.setCenterY(height / 2);

		elements.clear();

		// page number
		pageNumberLabel = new UILabel("1");
		pageNumberLabel.setCenterX(background.getCenterX());
		pageNumberLabel.setMinY(background.getMinY() + 156);
		elements.add(pageNumberLabel);

		initPages();

		int pageFlipButtonSpacing = 50;
		prevPageButton = addRenderableWidget(new PageFlipButton(
			background.getCenterX() - pageFlipButtonSpacing / 2 - PageFlipButton.width,
			background.getMinY() + 152,
			PageFlipButton.Direction.BACKWARD,
			this
		));
		nextPageButton = addRenderableWidget(new PageFlipButton(
			background.getCenterX() + pageFlipButtonSpacing / 2,
			background.getMinY() + 152,
			PageFlipButton.Direction.FORWARD,
			this
		));

		updateButtonVisibility();
	}

	private void initPages() {
		pages.clear();

		var history = FoodList.get(player).summarizeHistory();
		pages.add(new DiversityPage(history.diversity(), history.mealsEaten(), background.frame));
		pages.addAll(FoodListPage.fromHistory(background.frame, history.foods()));

		Pair<List<BenefitInfo>, List<BenefitInfo>> benefits = BenefitsHandler.getBenefitInfo(history.diversity(), history.mealsEaten());
		List<BenefitInfo> activeBenefits = benefits.getKey().stream().filter(bi -> !bi.detriment).toList();
		List<BenefitInfo> inactiveDetriments = benefits.getKey().stream().filter(bi -> bi.detriment).toList();
		List<BenefitInfo> inactiveBenefits = benefits.getValue().stream().filter(bi -> !bi.detriment).toList();
		List<BenefitInfo> activeDetriments = benefits.getValue().stream().filter(bi -> bi.detriment).toList();

		addPages("active_detriments_header", activeDetriments, inactiveRed);
		addPages("active_benefits_header", activeBenefits, activeGreen);

		if (SOLSweetPotatoConfig.shouldShowInactiveBenefits()) {
			addPages("inactive_detriments_header", inactiveDetriments, activeGreen);
			addPages("inactive_benefits_header", inactiveBenefits, inactiveRed);
		}
	}

	private void addPages(String headerLocalizationPath, List<BenefitInfo> benefitInfoList, Color activeColor) {
		String header = localized("gui", "food_book." + headerLocalizationPath);

		pages.addAll(BenefitsPage.pages(background.frame, header, benefitInfoList, activeColor));
	}

	@Override
	public void render(GuiGraphics matrices, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrices, mouseX, mouseY, partialTicks);

		UIElement.render(matrices, background, mouseX, mouseY);

		super.render(matrices, mouseX, mouseY, partialTicks);

		if (!pages.isEmpty()) { // might not be loaded yet; race condition
			// current page
			UIElement.render(matrices, elements, mouseX, mouseY);
			UIElement.render(matrices, pages.get(currentPageNumber), mouseX, mouseY);
		}
	}

	@Override
	public void switchToPage(int pageNumber) {
		if (!isWithinRange(pageNumber)) return;

		currentPageNumber = pageNumber;
		updateButtonVisibility();

		pageNumberLabel.text = "" + (currentPageNumber + 1);
	}

	@Override
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	@Override
	public boolean isWithinRange(int pageNumber) {
		return pageNumber >= 0 && pageNumber < pages.size();
	}

	private void updateButtonVisibility() {
		prevPageButton.updateState();
		nextPageButton.updateState();
	}
}
