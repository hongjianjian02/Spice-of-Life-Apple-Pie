/*
 * Modified 2026-11-15 by the hongjianjian02 fork: unofficial Minecraft 1.21.1
 * NeoForge port and associated regression tests / distribution notices.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato.utils;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.tracking.benefits.AttributeBenefit;
import com.tarinoita.solsweetpotato.tracking.benefits.Benefit;
import com.tarinoita.solsweetpotato.tracking.benefits.EffectBenefit;

import java.util.ArrayList;
import java.util.List;

public class BenefitsParser {
    public static List<List<Benefit>> parse(List<String> unparsed) {
        List<List<Benefit>> allBenefits = new ArrayList<>();

        int thresh = 0;
        for (String s : unparsed) {
            String[] thresholdBenefitsString = s.split(";", 0);
            List<Benefit> thresholdBenefits = new ArrayList<>();

            for (String benefitString : thresholdBenefitsString) {
                benefitString = benefitString.trim();
                if (benefitString.isEmpty()) continue;
                boolean is_detriment = false;
                switch(benefitString.charAt(0)){
                    case '-':
                        is_detriment = true;
                    case '+':
                        benefitString = benefitString.substring(1);
                    default:
                        break;
                }
                String[] benefitArgs = benefitString.split(",", 0);
                int len = benefitArgs.length;

                if (len < 2) {
                    SOLSweetPotato.LOGGER.warn("Invalid benefit specification: " + benefitString);
                    continue;
                }

                String benefitType;
                if (benefitArgs[0].equals("attribute")) {
                    if (len < 3) {
                        SOLSweetPotato.LOGGER.warn("Need to specify a value when defining an attribute benefit: " + benefitString);
                        continue;
                    }
                    benefitType = "attribute";
                } else if (benefitArgs[0].equals("effect")) {
                    benefitType = "effect";
                } else {
                    SOLSweetPotato.LOGGER.warn("Invalid benefit type: " + benefitArgs[0] + " in string " + benefitString);
                    continue;
                }

                String benefitName = benefitArgs[1];
                double benefitValue = 0;
                if (len > 2) {
                    try {
                        benefitValue = Double.parseDouble(benefitArgs[2]);
                        if (!Double.isFinite(benefitValue)) throw new NumberFormatException();
                    } catch (NumberFormatException exception) {
                        SOLSweetPotato.LOGGER.warn("Invalid benefit value: " + benefitString);
                        continue;
                    }
                }

                if (benefitType.equals("attribute")) {
                    thresholdBenefits.add(new AttributeBenefit(benefitName, benefitValue, thresh, is_detriment));
                }
                else if (benefitType.equals("effect")) {
                    thresholdBenefits.add(new EffectBenefit(benefitName, benefitValue, thresh, is_detriment));
                }
            }
            allBenefits.add(thresholdBenefits);
            thresh++;
        }

        return allBenefits;
    }
}
