package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.Nest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendNestFeedback(String feedback) implements CustomPacketPayload {

    public static final Type<SendNestFeedback> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_nest_feedback"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SendNestFeedback> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SendNestFeedback::feedback,
            SendNestFeedback::new
    );

    public static void handleClient(final SendNestFeedback packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientNestData.nestFeedback = packet.feedback;
        });
    }
}
