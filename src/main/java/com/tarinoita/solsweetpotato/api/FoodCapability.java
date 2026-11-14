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
