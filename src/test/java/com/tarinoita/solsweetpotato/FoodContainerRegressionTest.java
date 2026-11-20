/*
 * Added 2026-11-18 by the hongjianjian02 fork: lunch-container empty-slot sync regressions.
 * Original license and third-party notices retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato;

import com.tarinoita.solsweetpotato.item.SOLSweetPotatoItems;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerItem;
import com.tarinoita.solsweetpotato.item.foodcontainer.FoodSlot;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FoodContainerRegressionTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    static Stream<ItemStack> containers() {
        return Stream.of(SOLSweetPotatoItems.LUNCHBAG.get(), SOLSweetPotatoItems.LUNCHBOX.get(),
                SOLSweetPotatoItems.GOLDEN_LUNCHBOX.get()).map(ItemStack::new);
    }

    // Exercise the vanilla client packet path: AbstractContainerMenu.initializeContents ->
    // FoodSlot.set -> ComponentItemHandler.setStackInSlot, with no world/player dependency.
    private static class SyncMenu extends AbstractContainerMenu {
        SyncMenu(IItemHandlerModifiable handler) {
            super(null, 1);
            for (int i = 0; i < handler.getSlots(); i++) addSlot(new FoodSlot(handler, i, 0, 0));
        }
        @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
        @Override public boolean stillValid(Player player) { return true; }
    }

    @ParameterizedTest @MethodSource("containers")
    void emptyContentPacketDoesNotDisconnect(ItemStack holder) {
        var inventory = FoodContainerItem.getInventory(holder);
        var menu = new SyncMenu(inventory);
        assertDoesNotThrow(() -> menu.initializeContents(1,
                Collections.nCopies(inventory.getSlots(), ItemStack.EMPTY), ItemStack.EMPTY));
        for (int i = 0; i < inventory.getSlots(); i++) assertTrue(menu.getSlot(i).getItem().isEmpty());
    }

    @ParameterizedTest @MethodSource("containers")
    void mixedContentPacketAndLaterClearArePersisted(ItemStack holder) {
        var inventory = FoodContainerItem.getInventory(holder);
        var menu = new SyncMenu(inventory);
        var contents = new java.util.ArrayList<>(Collections.nCopies(inventory.getSlots(), ItemStack.EMPTY));
        contents.set(0, new ItemStack(Items.APPLE, 4));
        contents.set(inventory.getSlots() - 1, new ItemStack(Items.BREAD, 2));
        assertDoesNotThrow(() -> menu.initializeContents(2, contents, ItemStack.EMPTY));
        assertEquals(4, inventory.getStackInSlot(0).getCount());
        assertEquals(2, inventory.getStackInSlot(inventory.getSlots() - 1).getCount());
        assertDoesNotThrow(() -> menu.getSlot(0).set(ItemStack.EMPTY));
        assertTrue(holder.get(DataComponents.CONTAINER).getStackInSlot(0).isEmpty());
        var reloadedView = FoodContainerItem.getInventory(holder.copy());
        assertTrue(reloadedView.getStackInSlot(0).isEmpty());
        assertEquals(2, reloadedView.getStackInSlot(inventory.getSlots() - 1).getCount());
    }

    @ParameterizedTest @MethodSource("containers")
    void takingLastFoodAndClearingItsSlotDoesNotThrow(ItemStack holder) {
        var inventory = FoodContainerItem.getInventory(holder);
        var slot = new FoodSlot(inventory, 0, 0, 0);
        slot.set(new ItemStack(Items.APPLE));
        assertEquals(Items.APPLE, slot.remove(1).getItem());
        assertDoesNotThrow(() -> slot.set(ItemStack.EMPTY));
        assertTrue(slot.getItem().isEmpty());
        assertDoesNotThrow(() -> inventory.setStackInSlot(0, ItemStack.EMPTY));
    }

    @ParameterizedTest @MethodSource("containers")
    void emptyWritesDoNotWeakenFoodOnlyOrNestingRestrictions(ItemStack holder) {
        var inventory = FoodContainerItem.getInventory(holder);
        assertTrue(inventory.isItemValid(0, ItemStack.EMPTY));
        assertFalse(new FoodSlot(inventory, 0, 0, 0).mayPlace(ItemStack.EMPTY));
        for (ItemStack invalid : List.of(new ItemStack(Items.STONE), new ItemStack(Items.BOWL),
                new ItemStack(SOLSweetPotatoItems.LUNCHBAG.get()),
                new ItemStack(SOLSweetPotatoItems.LUNCHBOX.get()),
                new ItemStack(SOLSweetPotatoItems.GOLDEN_LUNCHBOX.get()))) {
            assertFalse(inventory.isItemValid(0, invalid));
            assertFalse(inventory.insertItem(0, invalid, false).isEmpty());
            assertThrows(RuntimeException.class, () -> inventory.setStackInSlot(0, invalid));
        }
        assertThrows(RuntimeException.class, () -> inventory.setStackInSlot(-1, ItemStack.EMPTY));
        assertThrows(RuntimeException.class, () -> inventory.setStackInSlot(inventory.getSlots(), ItemStack.EMPTY));
        assertTrue(inventory.getStackInSlot(0).isEmpty());
    }
}
