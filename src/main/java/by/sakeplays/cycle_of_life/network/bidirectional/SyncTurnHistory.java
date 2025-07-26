package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncTurnHistory(int playerId, float f) implements CustomPacketPayload {

    public static final Type<SyncTurnHistory> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_turn_history"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncTurnHistory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncTurnHistory::playerId,
            ByteBufCodecs.FLOAT, SyncTurnHistory::f,
            SyncTurnHistory::new
    );

    public static void handleServer(final SyncTurnHistory packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                Util.recordTurnHistory(player, packet.f());

            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }

    public static void handleClient(final SyncTurnHistory packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                Util.recordTurnHistory(player, packet.f());

            }
        });
    }


}
