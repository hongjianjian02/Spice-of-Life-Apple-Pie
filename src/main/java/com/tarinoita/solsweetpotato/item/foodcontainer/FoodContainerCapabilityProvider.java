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
        return FoodSlot.canHold(stack);
    }
}
