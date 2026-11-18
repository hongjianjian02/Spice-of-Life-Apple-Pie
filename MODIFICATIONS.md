# Modification notices

Maintainer: the hongjianjian02 fork. All dates use YYYY-MM-DD.

- 2026-11-15: ported the upstream 1.19.2 Forge checkout to Minecraft 1.21.1 NeoForge.
  Port commit: `a6cf5ae` ("移植到1.21.1").
- 2026-11-15: restored third-party attribution, added license distribution, source
  archives, unofficial-fork identification and release workflow safeguards.
- Original LGPL license text has not been changed. Modified Java files carry
  dated notices; build/config files use comments and JSON files use `_port_notice` metadata.
- Moved PNG files have identical contents. They are not redrawn or relicensed;
  their path/date notice is recorded below instead of changing the binary image.
- Gradle wrapper replacement retains its independent Apache-2.0 notices.

## Simplified Chinese localization — 2026-11-15

Maintainer: the hongjianjian02 fork. Added `zh_cn.json` and completed `en_us.json`
with matching translations for items, containers, the food book, tooltips, keybinds,
commands and all 16 configuration options (including tooltip explanations and categories).
English remains supported. Configuration option names/IDs and existing saves are unchanged.

- `src/main/resources/assets/solapplepie/lang/{en_us,zh_cn}.json`: 76 matching
  translation keys per locale; added dated `_port_notice` metadata.
- `src/main/java/com/tarinoita/solsweetpotato/client/SOLClientRegistry.java`:
  register keybind translation keys rather than text fixed to the startup language.
- `src/main/java/com/tarinoita/solsweetpotato/client/gui/BenefitsPage.java`:
  translate invalid attribute/effect labels.
- `src/main/java/com/tarinoita/solsweetpotato/client/gui/FoodBookScreen.java` and
  `PageFlipButton.java`: translate the screen title and narrated page buttons.
- `src/main/java/com/tarinoita/solsweetpotato/command/FoodListCommand.java`:
  format diversity to two decimal places before passing it to the translated component.
- `src/test/java/com/tarinoita/solsweetpotato/LocalizationRegressionTest.java`:
  validate language parity, placeholders, configuration coverage and localized components.
- `PORTING_1.21.1.md`: document language selection and localization scope.

All original licenses and attribution are retained; this localization does not
change the mod's license. This section supplements, rather than replaces, the
historical port inventory below.

## Port file inventory relative to upstream/1.19

| Path | Date | Change |
| --- | --- | --- |
| `.circleci/config.yml` | 2026-11-15 | Added / modified for the port |
| `.github/workflows/build.yml` | 2026-11-15 | Added / modified for the port |
| `.github/workflows/gradle-publish.yml` | 2026-11-15 | Added / modified for the port |
| `.gitignore` | 2026-11-15 | Added / modified for the port |
| `.travis.yml` | 2026-11-15 | Added / modified for the port |
| `PORTING_1.21.1.md` | 2026-11-15 | Added / modified for the port |
| `README.md` | 2026-11-15 | Added / modified for the port |
| `build.gradle` | 2026-11-15 | Added / modified for the port |
| `gradle.properties` | 2026-11-15 | Added / modified for the port |
| `gradle/wrapper/gradle-wrapper.jar` | 2026-11-15 | Added / modified for the port |
| `gradle/wrapper/gradle-wrapper.properties` | 2026-11-15 | Added / modified for the port |
| `gradlew` | 2026-11-15 | Added / modified for the port |
| `gradlew.bat` | 2026-11-15 | Added / modified for the port |
| `settings.gradle` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/ConfigHandler.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/SOLSweetPotato.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/SOLSweetPotatoConfig.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/api/FoodCapability.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/api/SOLSweetPotatoAPI.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/ClientEvents.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/ContainerScreenRegistry.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/SOLClientRegistry.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/TooltipHandler.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/BenefitsPage.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/FoodBookScreen.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/PageFlipButton.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/elements/UIBox.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/elements/UIElement.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/elements/UIFoodQueueItem.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/elements/UIImage.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/elements/UIItemStack.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/client/gui/elements/UILabel.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/command/FoodListCommand.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/communication/ConfigMessage.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/communication/FoodListMessage.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/integration/Origins.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/FoodBookItem.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/SOLSweetPotatoItems.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainer.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainerCapabilityProvider.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainerItem.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainerProvider.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainerScreen.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodSlot.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/lib/Localization.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/CapabilityHandler.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/FoodInstance.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/FoodList.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/FoodTracker.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/benefits/AttributeBenefit.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/benefits/BenefitsHandler.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/benefits/EffectBenefit.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/tracking/benefits/EffectBenefitsCapability.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/utils/BenefitsParser.java` | 2026-11-15 | Added / modified for the port |
| `src/main/java/com/tarinoita/solsweetpotato/utils/ComplexityParser.java` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/META-INF/mods.toml` | 2026-11-15 | Removed old-format file |
| `src/main/resources/META-INF/neoforge.mods.toml` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/assets/solapplepie/models/item/food_book.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/assets/solapplepie/models/item/golden_lunchbox.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/assets/solapplepie/models/item/lunchbag.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/assets/solapplepie/models/item/lunchbox.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/assets/solapplepie/textures/item/food_book.png` | 2026-11-15 | Relocated unchanged bytes from `src/main/resources/assets/solapplepie/textures/items/food_book.png`; no artwork alteration |
| `src/main/resources/assets/solapplepie/textures/item/golden_lunchbox.png` | 2026-11-15 | Relocated unchanged bytes from `src/main/resources/assets/solapplepie/textures/items/golden_lunchbox.png`; no artwork alteration |
| `src/main/resources/assets/solapplepie/textures/item/lunchbag.png` | 2026-11-15 | Relocated unchanged bytes from `src/main/resources/assets/solapplepie/textures/items/lunchbag.png`; no artwork alteration |
| `src/main/resources/assets/solapplepie/textures/item/lunchbox.png` | 2026-11-15 | Relocated unchanged bytes from `src/main/resources/assets/solapplepie/textures/items/lunchbox.png`; no artwork alteration |
| `src/main/resources/data/solapplepie/advancement/recipes/food_book.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/data/solapplepie/advancements/recipes/food_book.json` | 2026-11-15 | Removed old-format file |
| `src/main/resources/data/solapplepie/recipe/food_book.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/data/solapplepie/recipe/golden_lunchbox.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/data/solapplepie/recipe/lunchbag.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/data/solapplepie/recipe/lunchbox.json` | 2026-11-15 | Added / modified for the port |
| `src/main/resources/data/solapplepie/recipes/food_book.json` | 2026-11-15 | Removed old-format file |
| `src/main/resources/pack.mcmeta` | 2026-11-15 | Added / modified for the port |
| `src/test/java/com/tarinoita/solsweetpotato/PortRegressionTest.java` | 2026-11-15 | Added / modified for the port |

## Added distribution materials

`NOTICE.txt`, `THIRD_PARTY_NOTICES.md`, `MODIFICATIONS.md`, `RELEASING.md`, and
`licenses/` were added on 2026-11-15. `SOURCE_INFO.txt` is generated during the
build and contains the exact packaged source-input paths and SHA-256 values;
Git revision alone is not sufficient to identify uncommitted modifications.

## 2026-11-15: optional highest-diversity food history

Added `Miscellaneous.keepHighestDiversityFoods` (default false). In this mode, FoodList retains distinct eligible foods in its existing serialized history and counts only the highest-complexity `queueSize` foods without time decay. Added matching simulations, per-food contribution, ordered food-book view and English/Chinese labels. Existing recent-meal mode remains the default; existing recorded meals migrate without changing the NBT schema. Foods already expired before this update cannot be reconstructed. Regression tests cover ranking, repeated/lower meals, persistence, score edits, blacklist and normal mode. Modified FoodList.java, SOLSweetPotatoConfig.java, TooltipHandler.java, FoodBookScreen.java, FoodListPage.java, en_us.json, zh_cn.json and PortRegressionTest.java; added HighestDiversityRegressionTest.java.

## Repeated potion-effect reward tiers — 2026-11-17

- `tracking/benefits/EffectBenefitsCapability.java`: remove only the matching reward
  definition, so a locked Resistance IV tier no longer deletes unlocked Resistance III
  (or a later regeneration tier an earlier one). Keep the strongest unlocked effect
  amplifier when multiple active thresholds grant the same effect.
- `src/test/java/com/tarinoita/solsweetpotato/EffectBenefitsRegressionTest.java`:
  regressions for locked higher tiers, duplicate regeneration, upgrades/downgrades,
  configuration order and removal after NBT persistence. Existing NBT format retained.

## Lunch-container empty-slot synchronization — 2026-11-18

- `item/foodcontainer/FoodContainerCapabilityProvider.java`: accept empty stacks
  in handler validation so initial container-content packets, clearing slots and
  removing the last food do not throw `Invalid stack 0 minecraft:air`. Keep
  non-food and nested-container restrictions unchanged.
- `item/foodcontainer/FoodContainerItem.java`: clear the slot before routing
  bowls/bottles to player inventory, rather than first writing a forbidden non-food
  remainder into the food-only handler. Food remainders stay in the lunch container.
- `src/test/java/com/tarinoita/solsweetpotato/FoodContainerRegressionTest.java`:
  reproduce vanilla `initializeContents` and slot-clearing paths for all three lunch
  containers; verify persistence, food-only restrictions and index validation.
- `PORTING_1.21.1.md`: document the issue and replacement instructions. No item
  registry IDs, data-component schema, language resources or licenses were changed.
