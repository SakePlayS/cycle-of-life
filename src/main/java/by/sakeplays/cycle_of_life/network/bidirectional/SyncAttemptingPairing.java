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

public record SyncAttemptingPairing(boolean v, int playerID) implements CustomPacketPayload {

    public static final Type<SyncAttemptingPairing> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_attempting_pairing"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncAttemptingPairing> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncAttemptingPairing::v,
            ByteBufCodecs.INT, SyncAttemptingPairing::playerID,
            SyncAttemptingPairing::new
    );

    public static void handleClient(final SyncAttemptingPairing packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.ATTEMPTING_PAIRING, packet.v);
            }
        });
    }

    public static void handleServer(final SyncAttemptingPairing packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.ATTEMPTING_PAIRING, packet.v);
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
