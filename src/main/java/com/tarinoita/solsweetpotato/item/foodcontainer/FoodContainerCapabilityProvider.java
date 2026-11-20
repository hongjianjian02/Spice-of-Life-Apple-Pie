/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Modified 2026-11-18: accept empty stacks when synchronizing or clearing lunch-container slots.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
/**
 * Much of the following code was adapted from Cyclic's storage bag code.
 * Copyright for portions of the code are held by Samson Basset (Lothrazar)
 * as part of Cyclic, under the MIT license.
 */
package com.tarinoita.solsweetpotato.item.foodcontainer;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;

/** Live stack-bound storage using the vanilla persistent, synchronized container component. */
public final class FoodContainerCapabilityProvider extends ComponentItemHandler {
    public FoodContainerCapabilityProvider(ItemStack container, int slots) {
        super(container, DataComponents.CONTAINER, slots);
    }
    @Override public boolean isItemValid(int slot, ItemStack stack) {
        // setStackInSlot validates empty writes too (menu sync, removal, and consumption).
        // Keep FoodSlot's placement predicate food-only; EMPTY represents clearing, not insertion.
        return stack.isEmpty() || FoodSlot.canHold(stack);
    }
}
