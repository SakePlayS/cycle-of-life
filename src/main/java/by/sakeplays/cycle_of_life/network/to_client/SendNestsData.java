package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.network.ModCodecs;
import by.sakeplays.cycle_of_life.util.DataArrivalState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SendNestsData(List<Nest> nests) implements CustomPacketPayload {

    public static final Type<SendNestsData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "send_nest_data"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SendNestsData> STREAM_CODEC = StreamCodec.composite(
            Nest.NEST_CODEC.apply(ByteBufCodecs.list()), SendNestsData::nests,
            SendNestsData::new
    );

    public static void handleClient(final SendNestsData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().sendSystemMessage(Component.literal("Size upon arrival: " + packet.nests.size()));
            ClientNestData.nests = packet.nests;
            ClientNestData.dataArrivalState = DataArrivalState.ARRIVED;
        });
    }
}
