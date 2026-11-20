/* Modified 2026-11-20 by the hongjianjian02 fork: paginate food history directly. */
package com.tarinoita.solsweetpotato.client.gui;

import com.tarinoita.solsweetpotato.SOLSweetPotatoConfig;
import com.tarinoita.solsweetpotato.client.gui.elements.UIFoodQueueItem;
import com.tarinoita.solsweetpotato.tracking.FoodList.EatenFood;
import net.minecraft.world.item.ItemStack;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import static com.tarinoita.solsweetpotato.lib.Localization.localized;

public final class FoodListPage extends ItemListPage {
    private FoodListPage(Rectangle frame, String header, String tooltip, List<EatenFood> foods) {
        super(frame, header, foods, food -> new UIFoodQueueItem(new ItemStack(food.item()), food.mealsAgo()));
        setHeaderTooltip(tooltip);
    }

    static List<FoodListPage> fromHistory(Rectangle frame, List<EatenFood> foods) {
        boolean highest = SOLSweetPotatoConfig.keepHighestDiversityFoods();
        String header = localized("gui", highest ? "food_book.highest_food_queue_label" : "food_book.food_queue_label", foods.size());
        String tooltip = localized("gui", highest ? "food_book.queue.highest_food_queue_tooltip" : "food_book.queue.food_queue_tooltip");
        List<FoodListPage> pages = new ArrayList<>();
        for (int start = 0; start < foods.size(); start += itemsPerPage) {
            int end = Math.min(start + itemsPerPage, foods.size());
            pages.add(new FoodListPage(frame, header, tooltip, foods.subList(start, end)));
        }
        return pages;
    }
}
