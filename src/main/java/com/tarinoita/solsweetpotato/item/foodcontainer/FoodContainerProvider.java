/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.item.foodcontainer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import com.tarinoita.solsweetpotato.lib.Localization;

import javax.annotation.Nullable;

public class FoodContainerProvider implements MenuProvider {
    private String displayName;
    private final net.minecraft.world.InteractionHand hand;

    public FoodContainerProvider(String displayName, net.minecraft.world.InteractionHand hand) {
        this.displayName = displayName;
        this.hand = hand;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(Localization.keyString("item", "container." + displayName));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new FoodContainer(i, playerInventory, player, hand);
    }
}
