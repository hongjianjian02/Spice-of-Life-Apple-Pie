package com.tarinoita.solsweetpotato;

import com.tarinoita.solsweetpotato.item.SOLSweetPotatoItems;
import com.tarinoita.solsweetpotato.tracking.FoodInstance;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import com.tarinoita.solsweetpotato.tracking.benefits.AttributeBenefit;
import com.tarinoita.solsweetpotato.utils.BenefitsParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PortRegressionTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        net.neoforged.fml.config.ConfigTracker.INSTANCE.loadDefaultServerConfigs();
    }
    @BeforeEach void defaults() {
        SOLSweetPotatoConfig.SERVER.queueSize.set(10);
        SOLSweetPotatoConfig.SERVER.startDecay.set(2);
        SOLSweetPotatoConfig.SERVER.endDecay.set(8);
        SOLSweetPotatoConfig.SERVER.minContribution.set(0.2);
        SOLSweetPotatoConfig.SERVER.whitelist.set(List.of());
        SOLSweetPotatoConfig.SERVER.blacklist.set(List.of());
        ConfigHandler.complexityMap = new HashMap<>();
        ConfigHandler.complexityMap.put(new FoodInstance(Items.APPLE), 2.0);
        ConfigHandler.complexityMap.put(new FoodInstance(Items.BREAD), 3.0);
    }
    @Test void attachmentPersistenceRoundTrips() {
        class TestHolder extends net.neoforged.neoforge.attachment.AttachmentHolder {
            void load(net.minecraft.core.HolderLookup.Provider registries, net.minecraft.nbt.CompoundTag tag) {
                deserializeAttachments(registries, tag);
            }
        }
        var registries = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        var first = new TestHolder();
        first.getData(com.tarinoita.solsweetpotato.tracking.CapabilityHandler.FOOD).addFood(Items.APPLE);
        var second = new TestHolder(); second.load(registries, first.serializeAttachments(registries));
        assertEquals(first.getData(com.tarinoita.solsweetpotato.tracking.CapabilityHandler.FOOD).serializeNBT(),
            second.getData(com.tarinoita.solsweetpotato.tracking.CapabilityHandler.FOOD).serializeNBT());
    }
    @Test void legacyNutritionBalanceIsPreserved() {
        ConfigHandler.complexityMap.clear();
        SOLSweetPotatoConfig.SERVER.defaultContribution.set(1.0);
        assertEquals((4 + 4 * 0.3f) / 2 / 5, FoodList.getComplexity(new FoodInstance(Items.APPLE)), 1e-6);
    }
    @Test void modelTexturesExistUnderTheStitchedItemDirectory() throws Exception {
        for (String item : List.of("food_book", "lunchbag", "lunchbox", "golden_lunchbox")) {
            try (var stream = getClass().getClassLoader().getResourceAsStream("assets/solapplepie/models/item/" + item + ".json")) {
                assertNotNull(stream);
                var model = com.google.gson.JsonParser.parseString(new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
                assertEquals("solapplepie:item/" + item, model.getAsJsonObject().getAsJsonObject("textures").get("layer0").getAsString());
            }
            assertNotNull(getClass().getClassLoader().getResource("assets/solapplepie/textures/item/" + item + ".png"));
        }
    }
    @Test void foodDataRoundTrips() {
        FoodList original = new FoodList();
        original.addFood(Items.APPLE); original.addFood(Items.BREAD); original.addFood(Items.APPLE);
        FoodList loaded = new FoodList(); loaded.deserializeNBT(original.serializeNBT());
        assertEquals(original.serializeNBT(), loaded.serializeNBT());
        assertEquals(3, loaded.getFoodsEaten());
        assertEquals(0, loaded.getLastEaten(Items.APPLE));
        assertEquals(1, loaded.getLastEaten(Items.BREAD));
    }
    @Test void simulatedDiversityMatchesActualChangeAndExpires() {
        FoodList list = new FoodList(); list.addFood(Items.APPLE);
        double before = list.foodDiversity(); double change = list.simulateFoodAdd(Items.BREAD);
        list.addFood(Items.BREAD);
        assertEquals(before + change, list.foodDiversity(), 1e-9);
        for (int i = 0; i < 10; i++) list.addFood(Items.BREAD);
        assertFalse(list.hasEaten(Items.APPLE));
    }
    @Test void foodPayloadCodecRoundTrips() {
        FoodList list = new FoodList(); list.addFood(Items.APPLE);
        var message = new com.tarinoita.solsweetpotato.communication.FoodListMessage(list);
        var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),
            net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
        try {
            com.tarinoita.solsweetpotato.communication.FoodListMessage.STREAM_CODEC.encode(buffer, message);
            var decoded = com.tarinoita.solsweetpotato.communication.FoodListMessage.STREAM_CODEC.decode(buffer);
            assertEquals(message.capabilityNBT(), decoded.capabilityNBT());
        } finally { buffer.release(); }
    }
    @Test void missingOrMalformedFoodsAreIgnored() {
        assertNull(FoodInstance.decode("not a resource id"));
        assertNull(FoodInstance.decode("missingmod:removed_food"));
    }
    @Test void attributeIdsAreStableAndLegacyUuidCanBeRead() {
        var first = new AttributeBenefit("generic.max_health", 2, 0, false);
        var second = new AttributeBenefit("generic.max_health", 2, 0, false);
        assertEquals(first.serializeNBT().getString("id"), second.serializeNBT().getString("id"));
        var legacy = new AttributeBenefit("generic.max_health", 2, 0, "12345678-1234-1234-1234-123456789abc", false);
        assertTrue(legacy.serializeNBT().getString("id").startsWith("solapplepie:benefit/"));
        assertEquals(first.serializeNBT(), AttributeBenefit.fromNBT(first.serializeNBT()).serializeNBT());
    }
    @Test void malformedBenefitsDoNotCrashParsing() {
        var parsed = BenefitsParser.parse(List.of(";attribute,generic.max_health;effect,speed,NaN;effect,speed,not_a_number;effect,speed,0"));
        assertEquals(1, parsed.getFirst().size());
    }
    @Test void containersUseLivePersistentComponentsAndRejectNesting() {
        ItemStack holder = new ItemStack(SOLSweetPotatoItems.LUNCHBAG.get());
        var inventory = new com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerCapabilityProvider(holder, 5);
        assertTrue(inventory.insertItem(0, new ItemStack(Items.APPLE, 4), false).isEmpty());
        assertEquals(4, holder.get(DataComponents.CONTAINER).getStackInSlot(0).getCount());
        ItemStack copied = holder.copy();
        var copiedInventory = new com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerCapabilityProvider(copied, 5);
        assertEquals(4, copiedInventory.getStackInSlot(0).getCount());
        copiedInventory.extractItem(0, 1, false);
        assertEquals(3, copiedInventory.getStackInSlot(0).getCount());
        assertEquals(4, inventory.getStackInSlot(0).getCount());
        var secondView = new com.tarinoita.solsweetpotato.item.foodcontainer.FoodContainerCapabilityProvider(holder, 5);
        secondView.extractItem(0, 2, false);
        assertEquals(2, inventory.getStackInSlot(0).getCount());
        assertFalse(inventory.insertItem(1, new ItemStack(Items.STONE), false).isEmpty());
        assertFalse(inventory.insertItem(1, new ItemStack(SOLSweetPotatoItems.LUNCHBOX.get()), false).isEmpty());
        assertEquals(5, holder.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM).getSlots());
    }
}
