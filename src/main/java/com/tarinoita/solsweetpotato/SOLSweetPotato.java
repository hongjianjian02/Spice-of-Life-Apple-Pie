package com.tarinoita.solsweetpotato;

import com.tarinoita.solsweetpotato.client.ContainerScreenRegistry;
import com.tarinoita.solsweetpotato.communication.ConfigMessage;
import com.tarinoita.solsweetpotato.communication.FoodListMessage;
import com.tarinoita.solsweetpotato.tracking.CapabilityHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SOLSweetPotato.MOD_ID)
public final class SOLSweetPotato {
    public static final String MOD_ID = "solapplepie";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static boolean HasFarmersDelight() { return ModList.get().isLoaded("farmersdelight"); }
    public static boolean HasPamsHarvestcraft() { return ModList.get().isLoaded("pamhc2foodcore"); }
    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    public SOLSweetPotato(IEventBus modBus, ModContainer container) {
        SOLSweetPotatoConfig.setUp(container);
        ContainerScreenRegistry.MENU_TYPES.register(modBus);
        com.tarinoita.solsweetpotato.item.SOLSweetPotatoItems.ITEMS.register(modBus);
        CapabilityHandler.ATTACHMENTS.register(modBus);
        modBus.addListener(SOLSweetPotato::registerPayloads);
    }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("2");
        registrar.playToClient(FoodListMessage.TYPE, FoodListMessage.STREAM_CODEC, FoodListMessage::handle);
        registrar.playToClient(ConfigMessage.TYPE, ConfigMessage.STREAM_CODEC, ConfigMessage::handle);
    }
}
