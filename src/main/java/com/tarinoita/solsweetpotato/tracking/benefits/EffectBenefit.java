/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.tracking.benefits;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Each instance represents a specific potion effect. Handles logic for application to player.
 */
public final class EffectBenefit extends Benefit{
    private net.minecraft.core.Holder<MobEffect> effect;
    public static final int DEFAULT_DURATION_TICKS = 300;
    public static final int REFRESH_THRESHOLD_TICKS = 200;

    public EffectBenefit(String name, double value, double threshold, boolean detriment) {
        super("effect", name, value, threshold, detriment);
    }

    public void applyTo(Player player) {
        if (!checkUsage() || player.level().isClientSide)
            return;

        EffectBenefitsCapability effectBenefits = EffectBenefitsCapability.get(player);
        effectBenefits.addEffectBenefitUnique(this);
    }

    public boolean needsRefresh(Player player) {
        if (!checkUsage() || player.level().isClientSide)
            return false;

        MobEffectInstance currentEffect = player.getEffect(effect);
        return shouldRefresh(currentEffect != null,
                currentEffect == null ? -1 : currentEffect.getAmplifier(),
                currentEffect == null ? 0 : currentEffect.getDuration(),
                (int) value);
    }

    public static boolean shouldRefresh(boolean hasEffect, int currentAmplifier,
            int currentDuration, int benefitAmplifier) {
        if (!hasEffect || currentAmplifier < benefitAmplifier)
            return true;

        return currentAmplifier == benefitAmplifier
                && currentDuration <= REFRESH_THRESHOLD_TICKS;
    }

    public void refresh(Player player) {
        if (!checkUsage() || player.level().isClientSide)
            return;

        MobEffectInstance currentEffect = player.getEffect(effect);
        // Keep a stronger effect supplied by another source; do not downgrade it.
        if (currentEffect != null && currentEffect.getAmplifier() > (int) value)
            return;

        player.addEffect(new MobEffectInstance(effect, DEFAULT_DURATION_TICKS, (int) value, false, false));
    }

    public void removeFrom(Player player) {
        if (!checkUsage() || player.level().isClientSide)
            return;

        EffectBenefitsCapability effectBenefits = EffectBenefitsCapability.get(player);
        effectBenefits.removeEffectBenefit(this);
    }

    private boolean checkUsage() {
        if (invalid){
            return false;
        }

        if (effect == null) {
            createEffect();
            return !invalid;
        }

        return true;
    }

    private void createEffect() {
        try {
            effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(name)).orElse(null);
        }
        catch (ResourceLocationException e) {
            markInvalid();
            return;
        }

        if (effect == null) {
            markInvalid();
            return;
        }

        if (value < 0 || value > 255) {
            markInvalid();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        StringTag type = StringTag.valueOf(benefitType);
        StringTag n = StringTag.valueOf(name);
        DoubleTag v = DoubleTag.valueOf(value);
        DoubleTag thresh = DoubleTag.valueOf(threshold);
        ByteTag detr = ByteTag.valueOf((byte) (detriment ? 1 : 0));

        tag.put("type", type);
        tag.put("name", n);
        tag.put("value", v);
        tag.put("threshold", thresh);
        tag.put("detriment", detr);

        return tag;
    }

    public static EffectBenefit fromNBT(CompoundTag tag) {
        String type = tag.getString("type");
        if (!type.equals("effect")) {
            throw new RuntimeException("Mismatching benefit type");
        }
        String n = tag.getString("name");
        double v = tag.getDouble("value");
        double thresh = tag.getDouble("threshold");
        boolean detr = tag.getByte("detriment") == 1;

        return new EffectBenefit(n, v, thresh, detr);
    }
}
