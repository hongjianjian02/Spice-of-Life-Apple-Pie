/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.api;

import com.tarinoita.solsweetpotato.tracking.CapabilityHandler;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import net.minecraft.world.entity.player.Player;

/**
 Provides a stable API for interfacing with Spice of Life: Carrot Edition.
 */
public final class SOLSweetPotatoAPI
{

	private SOLSweetPotatoAPI() {}

	/**
	 Retrieves the {@link com.tarinoita.solsweetpotato.api.FoodCapability} for the given player.
	 */
	public static FoodCapability getFoodCapability(Player player) {
		return FoodList.get(player);
	}

	/**
	 Synchronizes the food list for the given player to the client, updating their max health in the process.
	 */
	public static void syncFoodList(Player player) {
		CapabilityHandler.syncFoodList(player);
	}
}
