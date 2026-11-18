/* Added 2026-11-17 by the hongjianjian02 fork; see LICENSE.txt. */
package com.tarinoita.solsweetpotato;

import com.tarinoita.solsweetpotato.tracking.benefits.EffectBenefit;
import com.tarinoita.solsweetpotato.tracking.benefits.EffectBenefitsCapability;
import com.tarinoita.solsweetpotato.utils.BenefitsParser;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EffectBenefitsRegressionTest {
    private EffectBenefit effect(String name, double level, double tier) {
        return new EffectBenefit(name, level, tier, false);
    }

    @Test void lockedResistanceFourDoesNotRemoveUnlockedResistanceThree() {
        var tiers = BenefitsParser.parse(List.of(
                "attribute,minecraft:generic.armor_toughness,4;effect,minecraft:resistance,2",
                "effect,minecraft:resistance,3"));
        var active = new EffectBenefitsCapability();
        // Reproduce updateBenefits at diversity 44: apply tier 44, remove locked tier 100.
        active.addEffectBenefitUnique((EffectBenefit) tiers.get(0).get(1));
        active.removeEffectBenefit((EffectBenefit) tiers.get(1).get(0));
        assertEquals(2.0, active.iterator().next().getValue());
    }

    @Test void resistanceUpgradesAtOneHundredAndDowngradesAfterRebuild() {
        var three = effect("minecraft:resistance", 2, 9);
        var four = effect("minecraft:resistance", 3, 19);
        var active = new EffectBenefitsCapability();
        active.addEffectBenefitUnique(three);
        active.addEffectBenefitUnique(four);
        assertEquals(four, active.iterator().next());
        assertEquals(1, active.serializeNBT().getList("effect_benefits", 10).size());
        active.clear();
        active.addEffectBenefitUnique(three);
        active.removeEffectBenefit(four);
        assertEquals(three, active.iterator().next());
    }

    @Test void lockedDuplicateRegenerationDoesNotRemoveEarlierTier() {
        var early = effect("minecraft:regeneration", 1, 2);
        var later = effect("minecraft:regeneration", 1, 5);
        var active = new EffectBenefitsCapability();
        active.addEffectBenefitUnique(early);
        active.removeEffectBenefit(later);
        assertEquals(early, active.iterator().next());
    }

    @Test void strongestUnlockedEffectWinsRegardlessOfConfigurationOrder() {
        var active = new EffectBenefitsCapability();
        var strong = effect("minecraft:resistance", 3, 0);
        active.addEffectBenefitUnique(strong);
        active.addEffectBenefitUnique(effect("minecraft:resistance", 2, 1));
        assertEquals(strong, active.iterator().next());
    }

    @Test void matchingBenefitCanBeRemovedAfterPersistenceRoundTrip() {
        var resistance = effect("minecraft:resistance", 2, 9);
        var nightVision = effect("minecraft:night_vision", 0, 3);
        var active = new EffectBenefitsCapability();
        active.addEffectBenefitUnique(resistance);
        active.addEffectBenefitUnique(nightVision);
        var restored = new EffectBenefitsCapability();
        restored.deserializeNBT(active.serializeNBT());
        restored.removeEffectBenefit(effect("minecraft:resistance", 2, 9));
        assertEquals(nightVision, restored.iterator().next());
        assertEquals(1, restored.serializeNBT().getList("effect_benefits", 10).size());
    }
}
