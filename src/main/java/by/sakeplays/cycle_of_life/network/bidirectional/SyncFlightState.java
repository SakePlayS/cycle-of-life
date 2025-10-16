package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFlightState(boolean flightState, int playerID) implements CustomPacketPayload {

    public static final Type<SyncFlightState> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_flight_state"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncFlightState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncFlightState::flightState,
            ByteBufCodecs.INT, SyncFlightState::playerID,
            SyncFlightState::new
    );

    public static void handleClient(final SyncFlightState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setFlying(packet.flightState);
            }
        });
    }

    public static void handleServer(final SyncFlightState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.getData(DataAttachments.DINO_DATA).setFlying(packet.flightState);
            PacketDistributor.sendToPlayersTrackingEntity(player, packet);
        });
    }
}
