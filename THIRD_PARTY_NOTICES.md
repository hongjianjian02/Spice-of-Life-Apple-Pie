# Third-party notices

Updated 2026-11-15 by the hongjianjian02 fork. This file records notices; it does not
replace or weaken any upstream license.

## Cyclic storage-bag code - MIT

Upstream Apple Pie identifies portions of these files as adapted from Cyclic:

- `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainer.java`
- `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainerScreen.java`
- `src/main/java/com/tarinoita/solsweetpotato/item/foodcontainer/FoodContainerCapabilityProvider.java`

The inherited file comments are retained verbatim, including the spelling
"Samson Basset". The verified Cyclic license spells the name "Samson Bassett".
Its copyright notice is: **Copyright (c) 2014-2018 Samson Bassett (Lothrazar)**.
The rewritten capability-provider file retains its historical attribution.

The complete unmodified license is in [licenses/Cyclic-MIT.txt](licenses/Cyclic-MIT.txt).
License source: Cyclic's 1.19.2 branch, commit `b1064d3057abaa547de0370398bf57dc6a9d50a4`,
[LICENSE.txt](https://github.com/Lothrazar/Cyclic/blob/b1064d3057abaa547de0370398bf57dc6a9d50a4/LICENSE.txt).
This is a verified branch snapshot, not a claim that every adapted line originated at that commit.

## Gradle Wrapper - Apache-2.0

`gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` are Gradle build
components, not original mod code. Their existing Apache-2.0 and copyright notices
are retained. [licenses/Gradle-Wrapper-Apache-2.0.txt](licenses/Gradle-Wrapper-Apache-2.0.txt)
is copied verbatim from `META-INF/LICENSE` in the bundled Gradle 9.2.1 wrapper JAR.

## External dependencies

Minecraft, NeoForge, ModDevGradle and test dependencies are resolved separately
by the build and retain their own licenses. They are not copied into the mod JAR
or the complete source archive by this project. These notices do not grant any
additional rights to Minecraft or any upstream project names/logos.
