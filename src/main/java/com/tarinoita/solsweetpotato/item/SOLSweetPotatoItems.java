/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.item;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = SOLSweetPotato.MOD_ID)
public final class SOLSweetPotatoItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SOLSweetPotato.MOD_ID);
    public static final DeferredItem<Item> FOOD_BOOK = ITEMS.register("food_book", FoodBookItem::new);
    public static final DeferredItem<FoodContainerItem> LUNCHBOX = ITEMS.register("lunchbox", () -> new FoodContainerItem(9, "lunchbox"));
    public static final DeferredItem<FoodContainerItem> LUNCHBAG = ITEMS.register("lunchbag", () -> new FoodContainerItem(5, "lunchbag"));
    public static final DeferredItem<FoodContainerItem> GOLDEN_LUNCHBOX = ITEMS.register("golden_lunchbox", () -> new FoodContainerItem(14, "golden_lunchbox"));
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> FoodContainerItem.getInventory(stack),
            LUNCHBOX.get(), LUNCHBAG.get(), GOLDEN_LUNCHBOX.get());
    }
    @SubscribeEvent
    public static void creativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(FOOD_BOOK); event.accept(LUNCHBAG); event.accept(LUNCHBOX); event.accept(GOLDEN_LUNCHBOX);
        }
    }
}
