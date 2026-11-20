/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Modified 2026-11-20: share meal transitions and expose a read-only history summary.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.tracking;

import com.tarinoita.solsweetpotato.ConfigHandler;
import com.tarinoita.solsweetpotato.SOLSweetPotatoConfig;
import com.tarinoita.solsweetpotato.api.FoodCapability;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public final class FoodList implements FoodCapability {
	private static final String NBT_KEY_FOOD_LIST = "foodList";
	private static final String NBT_KEY_UNIQUE_FOOD = "food";
	private static final String NBT_KEY_LAST_EATEN = "lastEaten";
	private static final String NBT_KEY_FOODS_EATEN = "foodsEaten";

	public static FoodList get(Player player) {
		return player.getData(CapabilityHandler.FOOD);
	}

	private static final int MAX_FOODS_EATEN = 1000;
	private int foodsEaten = 0;
	// Keys - foods eaten, Values - lastEaten, i.e. # meals ago the food was last eaten
	private final Map<FoodInstance, Integer> uniqueFoods = new HashMap<>();

	public FoodList() {}

	@Nullable
	private CompoundTag serializeUniqueFood(Map.Entry<FoodInstance, Integer> foodPair) {
		FoodInstance food = foodPair.getKey();
		Integer lastEaten = foodPair.getValue();

		String encodedFood = food.encode();
		if (encodedFood == null) {
			return null;
		}

		CompoundTag tag = new CompoundTag();
		StringTag s = StringTag.valueOf(encodedFood);
		FloatTag i = FloatTag.valueOf(lastEaten);
		tag.put(NBT_KEY_UNIQUE_FOOD, s);
		tag.put(NBT_KEY_LAST_EATEN, i);

		return tag;
	}

	/** used for persistent storage */
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();

		ListTag list = new ListTag();

		uniqueFoods.entrySet().stream()
			.map(this::serializeUniqueFood)
			.filter(Objects::nonNull)
			.forEach(list::add);
		tag.put(NBT_KEY_FOOD_LIST, list);
		tag.put(NBT_KEY_FOODS_EATEN, IntTag.valueOf(foodsEaten));

		return tag;
	}

	@Nullable
	private Pair<FoodInstance, Integer> deserializeUniqueFood(Pair<String, Float> encoded) {
		FoodInstance uniqueFood = FoodInstance.decode(encoded.getKey());
		Integer lastEaten = Math.round(encoded.getValue());

		if (uniqueFood == null)
			return null;

		return new ImmutablePair<>(uniqueFood, lastEaten);
	}

	/** used for persistent storage */
	@Override
	public void deserializeNBT(CompoundTag tag) {
		ListTag list = tag.getList(NBT_KEY_FOOD_LIST, Tag.TAG_COMPOUND);

		uniqueFoods.clear();
		list.stream()
			.map(nbt-> (CompoundTag) nbt)
			.map(nbt -> new ImmutablePair<>(nbt.getString(NBT_KEY_UNIQUE_FOOD), nbt.getFloat(NBT_KEY_LAST_EATEN)))
			.map(this::deserializeUniqueFood)
			.filter(Objects::nonNull)
			.forEach(pair -> uniqueFoods.put(pair.getKey(), pair.getValue()));
		foodsEaten = tag.getInt(NBT_KEY_FOODS_EATEN);
	}

	private static final int EXPIRED = -1;

	/** An excluded meal may advance the history without recording a new food. */
	private record Meal(@Nullable FoodInstance recordedFood, boolean keepHighest, int windowSize) {
		@Nullable
		static Meal forItem(Item food) {
			boolean allowed = SOLSweetPotatoConfig.shouldCount(food);
			if (!allowed && !SOLSweetPotatoConfig.shouldForbiddenCount()) return null;
			return new Meal(allowed ? new FoodInstance(food) : null,
					SOLSweetPotatoConfig.keepHighestDiversityFoods(), SOLSweetPotatoConfig.size());
		}

		int ageAfter(FoodInstance previousFood, int mealsAgo) {
			if (previousFood.equals(recordedFood)) return 0;
			int nextAge = mealsAgo + 1;
			if (keepHighest) return Math.min(MAX_FOODS_EATEN, nextAge);
			return nextAge >= windowSize ? EXPIRED : nextAge;
		}
	}

	public void addFood(Item food, Map<FoodInstance, Integer> foodMap) {
		Meal meal = Meal.forItem(food);
		if (meal == null) return;

		if (foodsEaten < MAX_FOODS_EATEN) foodsEaten++;
		var entries = foodMap.entrySet().iterator();
		while (entries.hasNext()) {
			var entry = entries.next();
			int nextAge = meal.ageAfter(entry.getKey(), entry.getValue());
			if (nextAge == EXPIRED) {
				entries.remove();
			} else {
				entry.setValue(nextAge);
			}
		}
		if (meal.recordedFood() != null) foodMap.putIfAbsent(meal.recordedFood(), 0);
	}

	public void addFood(Item food) {
		addFood(food, uniqueFoods);
	}

	/**
	 * @return The change in food diversity from eating the food.
	 */
	public double simulateFoodAdd(Item food) {
		Meal meal = Meal.forItem(food);
		if (meal == null) return 0.0;
		if (meal.keepHighest()) {
			// Ages do not affect ranking; only a new eligible food can change the score.
			if (meal.recordedFood() == null || uniqueFoods.containsKey(meal.recordedFood())) return 0.0;
			var projected = new HashMap<>(uniqueFoods);
			projected.put(meal.recordedFood(), 0);
			return foodDiversity(projected.entrySet()) - foodDiversity();
		}

		// Accumulate changes per food instead of subtracting two rounded totals. The
		// lunch container compares these deltas directly when choosing among its slots.
		double change = 0.0;
		for (var entry : uniqueFoods.entrySet()) {
			FoodInstance previousFood = entry.getKey();
			int nextAge = meal.ageAfter(previousFood, entry.getValue());
			double before = calculateDiversityContribution(previousFood, entry.getValue());
			if (nextAge == EXPIRED || previousFood.equals(meal.recordedFood())) {
				change -= before;
			} else {
				change += calculateDiversityContribution(previousFood, nextAge) - before;
			}
		}
		if (meal.recordedFood() != null) change += calculateDiversityContribution(meal.recordedFood(), 0);
		return change;
	}

	@Override
	public double foodDiversity() {
		return foodDiversity(uniqueFoods.entrySet());
	}

	public static double foodDiversity(Set<Map.Entry<FoodInstance, Integer>> foodData) {
		return sumContributions(contributingFoods(foodData));
	}

	private static double sumContributions(Set<Map.Entry<FoodInstance, Integer>> foods) {
		double diversity = 0;
		for (var entry : foods) {
			diversity += calculateDiversityContribution(entry.getKey(), entry.getValue());
		}
		return diversity;
	}

	public static double calculateDiversityContribution(FoodInstance food, int lastEaten) {
		if (SOLSweetPotatoConfig.keepHighestDiversityFoods()) {
			return SOLSweetPotatoConfig.shouldCount(food.getItem()) ? getComplexity(food) : 0.0;
		}
		return calculateTimePenalty(lastEaten) * getComplexity(food);
	}

	private static double calculateTimePenalty(int lastEaten) {
		int size = SOLSweetPotatoConfig.size();
		int startDecay = SOLSweetPotatoConfig.startDecay();
		int endDecay = SOLSweetPotatoConfig.endDecay();
		double minContribution = SOLSweetPotatoConfig.minContribution();

		if (startDecay > endDecay || startDecay < 0 || endDecay > size ||
				startDecay > size || minContribution > 1 || minContribution < 0) {
			// invalid
			return 0.0;
		}

		if (lastEaten <= startDecay) {
			return 1.0;
		}
		else if (lastEaten > endDecay) {
			return minContribution;
		}

		double slope = (1.0 - minContribution) / (double) (startDecay - endDecay);
		return slope * (lastEaten - startDecay) + 1.0;
	}

	public static double getComplexity(FoodInstance food) {
		if (ConfigHandler.complexityMap != null && ConfigHandler.complexityMap.containsKey(food)) {
			return ConfigHandler.complexityMap.get(food);
		}

		var foodProperties = food.item.getDefaultInstance().getFoodProperties(null);
		if (foodProperties != null)
		{
			var nutrition = foodProperties.nutrition();
			// 1.21 stores the actual saturation gain (2 * nutrition * modifier).
            // Preserve the 1.19.2 balance, which used nutrition * modifier.
            var saturation = foodProperties.saturation() / 2f;
			var average = ((saturation + nutrition) / 2);

			if (average < 5) {
				return average * SOLSweetPotatoConfig.defaultContribution() / 5f;
			}

			return SOLSweetPotatoConfig.defaultContribution() * 4 * Math.log10(average - 4) + 1;
		}

		return SOLSweetPotatoConfig.defaultContribution();
	}

	private static Set<Map.Entry<FoodInstance, Integer>> highestDiversityFoods(
			Set<Map.Entry<FoodInstance, Integer>> foodData) {
		List<Map.Entry<FoodInstance, Integer>> sorted = new ArrayList<>(foodData);
		sorted.removeIf(entry -> !SOLSweetPotatoConfig.shouldCount(entry.getKey().getItem()));
		sorted.sort(Comparator.<Map.Entry<FoodInstance, Integer>>comparingDouble(
				entry -> getComplexity(entry.getKey())).reversed()
				.thenComparing(entry -> entry.getKey().toString()));
		Set<Map.Entry<FoodInstance, Integer>> highest = new LinkedHashSet<>();
		for (int i = 0; i < Math.min(SOLSweetPotatoConfig.size(), sorted.size()); i++) {
			highest.add(new AbstractMap.SimpleImmutableEntry<>(sorted.get(i)));
		}
		return highest;
	}

	private static Set<Map.Entry<FoodInstance, Integer>> contributingFoods(Set<Map.Entry<FoodInstance, Integer>> foods) {
		return SOLSweetPotatoConfig.keepHighestDiversityFoods() ? highestDiversityFoods(foods) : foods;
	}

	public Set<Map.Entry<FoodInstance, Integer>> getData() {
		return contributingFoods(uniqueFoods.entrySet());
	}

	public record EatenFood(Item item, int mealsAgo) {}

	public record HistorySummary(double diversity, int mealsEaten, List<EatenFood> foods) {
		public HistorySummary {
			foods = List.copyOf(foods);
		}
	}

	/** Read the score and ordered foods from the same selection without exposing live map entries. */
	public HistorySummary summarizeHistory() {
		var selected = getData();
		List<EatenFood> foods = new ArrayList<>(selected.size());
		for (var entry : selected) {
			foods.add(new EatenFood(entry.getKey().getItem(), entry.getValue()));
		}
		if (!SOLSweetPotatoConfig.keepHighestDiversityFoods()) {
			foods.sort(Comparator.comparingInt(EatenFood::mealsAgo));
		}
		return new HistorySummary(sumContributions(selected), foodsEaten, foods);
	}

	public double getDiversityContribution(Item food) {
		FoodInstance instance = new FoodInstance(food);
		if (!uniqueFoods.containsKey(instance)) return 0.0;
		if (SOLSweetPotatoConfig.keepHighestDiversityFoods()
				&& getData().stream().noneMatch(entry -> entry.getKey().equals(instance))) return 0.0;
		return calculateDiversityContribution(instance, uniqueFoods.get(instance));
	}

	public int getLastEaten(Item food) {
		if (!hasEaten(food)) {
			return -1;
		}

		return uniqueFoods.get(new FoodInstance(food));
	}

	@Override
	public boolean hasEaten(Item food) {
		if (food.getDefaultInstance().getFoodProperties(null) == null) return false;
		return uniqueFoods.containsKey(new FoodInstance(food));
	}

	public void clearFood() {
		uniqueFoods.clear();
	}

	public Set<FoodInstance> getEatenFoods() {
		return uniqueFoods.keySet();
	}

	public int getFoodsEaten() {
		return foodsEaten;
	}

	public void resetFoodsEaten() {
		foodsEaten = 0;
	}

	public static class FoodListNotFoundException extends RuntimeException {
		public FoodListNotFoundException() {
			super("Player must have food capability attached, but none was found.");
		}
	}
}
