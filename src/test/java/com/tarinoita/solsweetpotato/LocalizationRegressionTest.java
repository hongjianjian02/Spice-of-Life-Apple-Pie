/*
 * Added 2026-11-15 by the hongjianjian02 fork: Simplified Chinese localization regression tests.
 * Original authorship and license notices are retained; see LICENSE.txt and THIRD_PARTY_NOTICES.md.
 */
package com.tarinoita.solsweetpotato;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.gson.JsonParser;
import com.tarinoita.solsweetpotato.lib.Localization;
import net.minecraft.SharedConstants;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class LocalizationRegressionTest {
    private static final Pattern PLACEHOLDER = Pattern.compile("%(?:[0-9]+\\$)?s");

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static Map<String, String> translations(String locale) throws Exception {
        String path = "assets/solapplepie/lang/" + locale + ".json";
        try (var stream = LocalizationRegressionTest.class.getClassLoader().getResourceAsStream(path)) {
            assertNotNull(stream, "Missing language resource: " + path);
            var json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            Map<String, String> result = new LinkedHashMap<>();
            json.entrySet().forEach(entry -> {
                assertTrue(entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString(), entry.getKey());
                result.put(entry.getKey(), entry.getValue().getAsString());
            });
            assertTrue(result.remove("_port_notice").contains("2026-11-15"));
            return result;
        }
    }

    @Test
    void englishAndChineseHaveIdenticalNonemptyKeys() throws Exception {
        var english = translations("en_us");
        var chinese = translations("zh_cn");
        assertEquals(english.keySet(), chinese.keySet());
        for (var locale : List.of(english, chinese)) {
            locale.forEach((key, value) -> {
                assertFalse(value.isBlank(), key);
                assertFalse(value.contains("\uFFFD"), key);
            });
        }
        assertEquals("食物手册", chinese.get("item.solapplepie.food_book"));
        assertEquals("午餐袋", chinese.get("item.solapplepie.lunchbag"));
        assertEquals("午餐盒", chinese.get("item.solapplepie.lunchbox"));
        assertEquals("金午餐盒", chinese.get("item.solapplepie.golden_lunchbox"));
    }

    @Test
    void placeholdersMatchAndAreSafeForTranslatedComponents() throws Exception {
        var english = translations("en_us");
        var chinese = translations("zh_cn");
        for (var entry : english.entrySet()) {
            String key = entry.getKey();
            String translated = chinese.get(key);
            assertEquals(PLACEHOLDER.matcher(entry.getValue()).results().map(r -> r.group()).toList(),
                    PLACEHOLDER.matcher(translated).results().map(r -> r.group()).toList(), key);
            for (String value : List.of(entry.getValue(), translated)) {
                assertFalse(PLACEHOLDER.matcher(value).replaceAll("").contains("%"), key);
                assertDoesNotThrow(() -> String.format(Locale.ROOT, value, "4", "8"), key);
            }
        }
    }

    private static void verifyConfigKeys(UnmodifiableConfig config, Map<String, String> translations) {
        for (Object entry : config.valueMap().values()) {
            if (entry instanceof UnmodifiableConfig nested) {
                verifyConfigKeys(nested, translations);
            } else if (entry instanceof ModConfigSpec.ValueSpec value) {
                String key = value.getTranslationKey();
                assertNotNull(key);
                assertTrue(translations.containsKey(key), "Missing config label: " + key);
                assertTrue(translations.containsKey(key + ".tooltip"), "Missing config tooltip: " + key);
            } else {
                fail("Unexpected configuration entry: " + entry);
            }
        }
    }

    @Test
    void allActualConfigurationLabelsTooltipsAndCategoriesAreTranslated() throws Exception {
        for (String locale : List.of("en_us", "zh_cn")) {
            var language = translations(locale);
            verifyConfigKeys(SOLSweetPotatoConfig.SERVER_SPEC.getSpec(), language);
            verifyConfigKeys(SOLSweetPotatoConfig.CLIENT_SPEC.getSpec(), language);
            for (String category : List.of("Benefits", "Filtering", "Miscellaneous", "Advanced", "Complexity", "miscellaneous")) {
                assertTrue(language.containsKey("solapplepie.configuration." + category), category);
            }
        }
    }

    private static Language language(Map<String, String> translations) {
        return new Language() {
            @Override public String getOrDefault(String key, String fallback) {
                return translations.getOrDefault(key, fallback);
            }
            @Override public boolean has(String key) { return translations.containsKey(key); }
            @Override public boolean isDefaultRightToLeft() { return false; }
            @Override public FormattedCharSequence getVisualOrder(FormattedText text) {
                return FormattedCharSequence.forward(text.getString(), Style.EMPTY);
            }
        };
    }

    @Test
    void componentsResolveChineseAndUpdateWhenLanguageChanges() throws Exception {
        Language previous = Language.getInstance();
        Component title = Component.translatable("item.solapplepie.food_book");
        Component command = Localization.localizedComponent("command", "solapplepie.diversity_feedback",
                String.format(Locale.ROOT, "%.2f", 12.345));
        try {
            Language.inject(language(translations("zh_cn")));
            assertEquals("食物手册", title.getString());
            assertEquals("饮食多样性：12.35", command.getString());
            assertEquals("还需进食 3 次", Localization.localizedComponent("gui", "food_book.stats.min_warning1", 3).getString());
            assertEquals("上次食用：1 次进食前", Localization.localizedComponent("gui", "food_book.queue.tooltip.last_eaten_label_singular", 1).getString());
            assertEquals("上一页", Localization.localizedComponent("gui", "food_book.previous_page").getString());
            assertEquals("下一页", Localization.localizedComponent("gui", "food_book.next_page").getString());
            assertEquals("无效：example:unknown", Localization.localizedComponent("gui", "food_book.benefits.invalid", "example:unknown").getString());
            Language.inject(language(translations("en_us")));
            assertEquals("Food Book", title.getString());
            assertEquals("Food Diversity: 12.35", command.getString());
        } finally {
            Language.inject(previous);
        }
    }

    @Test
    void pageCountersAndSingularPluralMealTooltipsKeepTheirArguments() throws Exception {
        var language = translations("zh_cn");
        assertEquals("3/12", String.format(Locale.ROOT, language.get("gui.solapplepie.food_book.fraction"), 3, 12));
        assertEquals("上次食用：5 次进食前", String.format(Locale.ROOT,
                language.get("gui.solapplepie.food_book.queue.tooltip.last_eaten_label"), 5));
        assertEquals("上次食用：1 次进食前", String.format(Locale.ROOT,
                language.get("gui.solapplepie.food_book.queue.tooltip.last_eaten_label_singular"), 1));
    }
}
