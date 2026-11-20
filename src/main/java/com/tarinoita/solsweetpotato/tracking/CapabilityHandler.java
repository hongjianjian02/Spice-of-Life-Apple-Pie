/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.tracking;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.SOLSweetPotatoConfig;
import com.tarinoita.solsweetpotato.communication.FoodListMessage;
import com.tarinoita.solsweetpotato.tracking.benefits.BenefitsHandler;
import com.tarinoita.solsweetpotato.tracking.benefits.EffectBenefitsCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

@EventBusSubscriber(modid = SOLSweetPotato.MOD_ID)
public final class CapabilityHandler {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SOLSweetPotato.MOD_ID);
    public static final Supplier<AttachmentType<FoodList>> FOOD = ATTACHMENTS.register("food", () ->
        AttachmentType.builder(FoodList::new).serialize(CompoundTag.CODEC.xmap(tag -> {
            FoodList list = new FoodList(); list.deserializeNBT(tag); return list;
        }, FoodList::serializeNBT)).build());
    public static final Supplier<AttachmentType<EffectBenefitsCapability>> EFFECT_BENEFITS = ATTACHMENTS.register(
        "effect_benefits", () -> AttachmentType.builder(EffectBenefitsCapability::new).build());
    @SubscribeEvent
    public static void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncFoodList(event.getEntity());
    }
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !SOLSweetPotatoConfig.shouldResetOnDeath()) {
            FoodList.get(event.getEntity()).deserializeNBT(FoodList.get(event.getOriginal()).serializeNBT());
        }
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        BenefitsHandler.updatePlayer(event.getEntity());
        syncFoodList(event.getEntity());
    }
    public static void syncFoodList(Player player) {
        if (player instanceof ServerPlayer target) {
            PacketDistributor.sendToPlayer(target, new FoodListMessage(FoodList.get(player)));
        }
    }
}
