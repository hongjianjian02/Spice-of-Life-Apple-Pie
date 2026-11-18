/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Modified 2026-11-17: preserve unlocked effect tiers and select the strongest active level.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.tracking.benefits;

import com.tarinoita.solsweetpotato.tracking.CapabilityHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EffectBenefitsCapability implements Iterable<EffectBenefit> {
    private final Set<EffectBenefit> effectBenefits = new HashSet<>();

    private static final String NBT_KEY_EFFECT_BENEFITS = "effect_benefits";

    public static EffectBenefitsCapability get(Player player) {
        return player.getData(CapabilityHandler.EFFECT_BENEFITS);
    }

    public void addEffectBenefit(EffectBenefit b) {
        effectBenefits.add(b);
    }

    public void addEffectBenefitUnique(EffectBenefit b) {
        // Multiple unlocked thresholds may grant the same effect; retain its strongest level.
        if (effectBenefits.stream().anyMatch(other -> other.getName().equals(b.getName())
                && other.getValue() > b.getValue())) {
            return;
        }
        effectBenefits.removeIf(other -> other.getName().equals(b.getName()));
        addEffectBenefit(b);
    }

    public void removeEffectBenefit(EffectBenefit b) {
        // A locked higher threshold must not remove an unlocked benefit with the same effect.
        effectBenefits.remove(b);
    }

    public void clear() {
        effectBenefits.clear();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag list = new ListTag();
        effectBenefits.stream().map(EffectBenefit::serializeNBT).forEach(list::add);
        tag.put(NBT_KEY_EFFECT_BENEFITS, list);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
    	ListTag list = tag.getList(NBT_KEY_EFFECT_BENEFITS, Tag.TAG_COMPOUND);

        effectBenefits.clear();
        list.stream()
                .map(nbt-> (CompoundTag) nbt)
                .map(EffectBenefit::fromNBT)
                .forEach(effectBenefits::add);
    }

    @Nonnull
    @Override
    public Iterator<EffectBenefit> iterator() {
        return effectBenefits.iterator();
    }

    public static class EffectsBenefitsNotFoundException extends RuntimeException {
        public EffectsBenefitsNotFoundException() {
            super("Player must have effect benefits capability attached, but none was found.");
        }
    }
}
