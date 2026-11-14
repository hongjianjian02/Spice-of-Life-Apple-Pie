package com.tarinoita.solsweetpotato.communication;
import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.ConfigHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
public record ConfigMessage(CompoundTag configNBT) implements CustomPacketPayload {
    public static final Type<ConfigMessage> TYPE = new Type<>(SOLSweetPotato.resourceLocation("configmessage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigMessage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG, ConfigMessage::configNBT, ConfigMessage::new);
    public ConfigMessage() { this(ConfigHandler.serializeConfig()); }
    @Override public Type<ConfigMessage> type() { return TYPE; }
    public static void handle(ConfigMessage message, IPayloadContext context) {
        ConfigHandler.deserializeConfig(message.configNBT);
    }
}
