package com.tarinoita.solsweetpotato.communication;
import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.tarinoita.solsweetpotato.tracking.FoodList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
public record FoodListMessage(CompoundTag capabilityNBT) implements CustomPacketPayload {
    public static final Type<FoodListMessage> TYPE = new Type<>(SOLSweetPotato.resourceLocation("foodlistmessage"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodListMessage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG, FoodListMessage::capabilityNBT, FoodListMessage::new);
    public FoodListMessage(FoodList list) { this(list.serializeNBT()); }
    @Override public Type<FoodListMessage> type() { return TYPE; }
    public static void handle(FoodListMessage message, IPayloadContext context) {
        FoodList.get(context.player()).deserializeNBT(message.capabilityNBT);
    }
}
