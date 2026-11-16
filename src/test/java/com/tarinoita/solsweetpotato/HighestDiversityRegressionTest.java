/* Added 2026-11-15 by the hongjianjian02 fork; see LICENSE.txt. */
package com.tarinoita.solsweetpotato;

import com.tarinoita.solsweetpotato.tracking.FoodInstance;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HighestDiversityRegressionTest {
    private final List<Item> foods = List.of(Items.APPLE, Items.BREAD, Items.CARROT,
            Items.POTATO, Items.BAKED_POTATO, Items.COOKED_BEEF, Items.COOKED_PORKCHOP,
            Items.COOKIE, Items.MELON_SLICE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE);

    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        net.neoforged.fml.config.ConfigTracker.INSTANCE.loadDefaultServerConfigs();
    }
    @BeforeEach void settings() {
        SOLSweetPotatoConfig.SERVER.keepHighestDiversityFoods.set(true);
        SOLSweetPotatoConfig.SERVER.queueSize.set(8);
        SOLSweetPotatoConfig.SERVER.whitelist.set(List.of());
        SOLSweetPotatoConfig.SERVER.blacklist.set(List.of());
        SOLSweetPotatoConfig.SERVER.shouldForbiddenCount.set(false);
        // Deliberately enable decay: highest-food mode must ignore it.
        SOLSweetPotatoConfig.SERVER.startDecay.set(0);
        SOLSweetPotatoConfig.SERVER.endDecay.set(8);
        SOLSweetPotatoConfig.SERVER.minContribution.set(0.0);
        ConfigHandler.complexityMap = new HashMap<>();
        for (int i = 0; i < foods.size(); i++)
            ConfigHandler.complexityMap.put(new FoodInstance(foods.get(i)), (double) i + 1);
    }
    @AfterEach void restoreMode() {
        SOLSweetPotatoConfig.SERVER.keepHighestDiversityFoods.set(false);
    }
    @Test void scoresUseHighestEightDistinctFoodsAndNeverDecay() {
        FoodList list = new FoodList();
        foods.forEach(list::addFood);
        assertEquals(60.0, list.foodDiversity()); // 4 + 5 + ... + 11
        assertEquals(8, list.getData().size());
        assertEquals(11, list.getEatenFoods().size());
        for (int i = 0; i < 1050; i++) list.addFood(Items.APPLE);
        assertEquals(60.0, list.foodDiversity());
        assertEquals(1000, list.getFoodsEaten());
        assertEquals(0.0, list.getDiversityContribution(Items.APPLE));
        assertEquals(11.0, list.getDiversityContribution(Items.GOLDEN_APPLE));
        assertTrue(list.hasEaten(Items.APPLE));
    }
    @Test void predictionMatchesReplacingTheLowestFoodWithoutMutatingHistoryOrCount() {
        FoodList list = new FoodList();
        foods.subList(0, 8).forEach(list::addFood);
        var saved = list.serializeNBT();
        assertEquals(10.0, list.simulateFoodAdd(Items.GOLDEN_APPLE)); // 11 replaces 1
        assertEquals(saved, list.serializeNBT());
        assertEquals(8, list.getFoodsEaten());
        list.addFood(Items.GOLDEN_APPLE);
        assertEquals(46.0, list.foodDiversity());
        assertEquals(0.0, list.simulateFoodAdd(Items.GOLDEN_APPLE));
        assertEquals(0.0, list.simulateFoodAdd(Items.APPLE));
    }
    @Test void persistenceKeepsHistoryAndComplexityChangesRecomputeTheTopEight() {
        FoodList original = new FoodList(); foods.forEach(original::addFood);
        FoodList loaded = new FoodList(); loaded.deserializeNBT(original.serializeNBT());
        assertEquals(original.serializeNBT(), loaded.serializeNBT());
        assertEquals(60.0, loaded.foodDiversity());
        assertEquals(11, loaded.getFoodsEaten());
        ConfigHandler.complexityMap.put(new FoodInstance(Items.APPLE), 100.0);
        assertEquals(156.0, loaded.foodDiversity()); // 100 + 5 + ... + 11
        assertEquals(100.0, loaded.getDiversityContribution(Items.APPLE));
        assertEquals(0.0, loaded.getDiversityContribution(Items.POTATO));
    }
    @Test void equalScoresHaveStableOrderingAndRemainDistinct() {
        ConfigHandler.complexityMap.replaceAll((food, value) -> 8.0);
        FoodList list = new FoodList(); foods.forEach(list::addFood);
        assertEquals(64.0, list.foodDiversity());
        var first = list.getData().stream().map(e -> e.getKey().encode()).toList();
        for (int i = 0; i < 20; i++) list.addFood(Items.GOLDEN_APPLE);
        assertEquals(first, list.getData().stream().map(e -> e.getKey().encode()).toList());
    }
    @Test void filteringAppliesToStoredFoodsAndBlacklistedMealsDoNotIncrementCount() {
        FoodList list = new FoodList(); foods.forEach(list::addFood);
        SOLSweetPotatoConfig.SERVER.blacklist.set(List.of("minecraft:golden_apple"));
        var before = list.serializeNBT();
        assertEquals(52.0, list.foodDiversity()); // 3 through 10
        assertEquals(0.0, list.simulateFoodAdd(Items.GOLDEN_APPLE));
        list.addFood(Items.GOLDEN_APPLE);
        assertEquals(before, list.serializeNBT());
        SOLSweetPotatoConfig.SERVER.whitelist.set(List.of("minecraft:apple"));
        assertEquals(1.0, list.foodDiversity());
    }
    @Test void normalModeStillExpiresRecentMeals() {
        SOLSweetPotatoConfig.SERVER.keepHighestDiversityFoods.set(false);
        FoodList list = new FoodList(); list.addFood(Items.APPLE);
        for (int i = 0; i < 8; i++) list.addFood(Items.BREAD);
        assertFalse(list.hasEaten(Items.APPLE));
        assertEquals(2.0, list.foodDiversity());
    }
    @Test void existingRecentHistoryAndMealCountLoadWithoutReset() {
        SOLSweetPotatoConfig.SERVER.keepHighestDiversityFoods.set(false);
        FoodList recent = new FoodList(); recent.addFood(Items.APPLE); recent.addFood(Items.BREAD);
        var oldTag = recent.serializeNBT();
        SOLSweetPotatoConfig.SERVER.keepHighestDiversityFoods.set(true);
        FoodList loaded = new FoodList(); loaded.deserializeNBT(oldTag);
        assertEquals(oldTag, loaded.serializeNBT());
        assertEquals(2, loaded.getFoodsEaten());
        assertEquals(3.0, loaded.foodDiversity());
        for (int i = 0; i < 20; i++) loaded.addFood(Items.BREAD);
        assertEquals(3.0, loaded.foodDiversity());
    }
}
