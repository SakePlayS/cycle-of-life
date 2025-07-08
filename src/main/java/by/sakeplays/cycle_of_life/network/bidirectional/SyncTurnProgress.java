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

public record SyncTurnProgress(float v, int playerID) implements CustomPacketPayload {

    public static final Type<SyncTurnProgress> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_turn_progress"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncTurnProgress> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SyncTurnProgress::v,
            ByteBufCodecs.INT, SyncTurnProgress::playerID,
            SyncTurnProgress::new
    );

    public static void handleClient(final SyncTurnProgress packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.TURN_PROGRESS, packet.v());
            }
        });
    }

    public static void handleServer(final SyncTurnProgress packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.TURN_PROGRESS, packet.v());
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
