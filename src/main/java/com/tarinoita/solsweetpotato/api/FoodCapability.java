/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

/** Simplified view of the food list. Use SOLSweetPotatoAPI to obtain a player's data. */
public interface FoodCapability {
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
    /** @return whether the given food is being tracked. */
    boolean hasEaten(Item item);
    /** @return the diversity score of the current food queue. */
    double foodDiversity();
}
