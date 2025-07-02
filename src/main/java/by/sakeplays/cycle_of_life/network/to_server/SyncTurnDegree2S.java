package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncTurnDegree2S(float turnDegree) implements CustomPacketPayload {

    public static final Type<SyncTurnDegree2S> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_turn_degree_2s"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncTurnDegree2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SyncTurnDegree2S::turnDegree,
            SyncTurnDegree2S::new
    );

    public static void handleServer(final SyncTurnDegree2S packet, final IPayloadContext context) {
        context.enqueueWork(() -> context.player().getData(DataAttachments.DINO_DATA).setTurnDegree(packet.turnDegree()));
    }
}
