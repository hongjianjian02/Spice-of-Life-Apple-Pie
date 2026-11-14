/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.integration;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/** Optional bridge for Origins forks retaining the Edwin Mindcraft API. */
public final class Origins {
    private static final Map<Player, Boolean> restricted = new WeakHashMap<>();
    private static boolean warned;
    public static void cacheInvalidate(Player player) { restricted.remove(player); }
    public static void clearCache() { restricted.clear(); }
    public static boolean hasRestrictedDiet(Player player) {
        return restricted.computeIfAbsent(player, Origins::query);
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean query(Player player) {
        try {
            Class<?> containerApi = Class.forName("io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer");
            Object optional = containerApi.getMethod("get", net.minecraft.world.entity.Entity.class).invoke(null, player);
            Object container = optional instanceof Optional<?> value ? value.orElse(null)
                : optional.getClass().getMethod("orElse", Object.class).invoke(optional, new Object[]{null});
            if (container == null) return false;
            Object registry = Class.forName("io.github.edwinmindcraft.origins.api.OriginsAPI")
                .getMethod("getOriginsRegistry").invoke(null);
            Map<?, ?> origins = (Map<?, ?>) containerApi.getMethod("getOrigins").invoke(container);
            for (Object key : origins.values()) {
                Object origin = ((Registry<?>) registry).get((ResourceKey) key);
                if (origin != null && (origin.toString().contains("[origins:vegetarian]")
                    || origin.toString().contains("[origins:carnivore]"))) return true;
            }
        } catch (ReflectiveOperationException | LinkageError | ClassCastException exception) {
            if (!warned) {
                warned = true;
                SOLSweetPotato.LOGGER.warn("Installed Origins fork does not expose the legacy diet API; lunch container diet restrictions are unavailable.", exception);
            }
        }
        return false;
    }
}
