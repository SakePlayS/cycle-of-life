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

public record SyncRestingState(int restingState, int playerID) implements CustomPacketPayload {

    public static final Type<SyncRestingState> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_resting_state"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncRestingState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncRestingState::restingState,
            ByteBufCodecs.INT, SyncRestingState::playerID,
            SyncRestingState::new
    );

    public static void handleClient(final SyncRestingState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.RESTING_STATE, packet.restingState);
            }
        });
    }

    public static void handleServer(final SyncRestingState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.RESTING_STATE, packet.restingState);
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
