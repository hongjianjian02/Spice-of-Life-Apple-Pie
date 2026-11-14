/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.client;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainer;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ContainerScreenRegistry {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, SOLSweetPotato.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<FoodContainer>> FOOD_CONTAINER =
            MENU_TYPES.register("food_container",
                    () -> IMenuTypeExtension.create(((windowId, inv, data) -> new FoodContainer(windowId, inv, inv.player, data.readEnum(net.minecraft.world.InteractionHand.class)))));
}
