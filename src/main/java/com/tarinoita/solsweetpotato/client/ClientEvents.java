/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.client;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.client.gui.FoodBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static com.tarinoita.solsweetpotato.client.SOLClientRegistry.OPEN_FOOD_BOOK;

@EventBusSubscriber(value = Dist.CLIENT, modid = SOLSweetPotato.MOD_ID)
public class ClientEvents {
    @SubscribeEvent
    public static void handleKeypress(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        if (OPEN_FOOD_BOOK != null && OPEN_FOOD_BOOK.consumeClick()) {
            FoodBookScreen.open(player);
        }
    }
}
