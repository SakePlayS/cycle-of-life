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

public record SyncKnockdownTime(int playerId, int value) implements CustomPacketPayload {

    public static final Type<SyncKnockdownTime> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_knockdown_time"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncKnockdownTime> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncKnockdownTime::playerId,
            ByteBufCodecs.INT, SyncKnockdownTime::value,
            SyncKnockdownTime::new
    );

    public static void handleClient(final SyncKnockdownTime packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.setData(DataAttachments.KNOCKDOWN_TIME, packet.value());

            }
        });
    }

    public static void handleServer(final SyncKnockdownTime packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                player.setData(DataAttachments.KNOCKDOWN_TIME, packet.value());
            }
        }).thenRun(() -> PacketDistributor.sendToAllPlayers(packet));
    }
}
