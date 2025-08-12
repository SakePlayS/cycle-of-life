package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.network.ModCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFullNestData(Nest nest) implements CustomPacketPayload {

    public static final Type<SyncFullNestData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_full_nest_data"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncFullNestData> STREAM_CODEC = StreamCodec.composite(
            ModCodecs.NEST_CODEC, SyncFullNestData::nest,
            SyncFullNestData::new
    );

    public static void handleClient(final SyncFullNestData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientNestData.ownNest = packet.nest;
        });
    }
}
