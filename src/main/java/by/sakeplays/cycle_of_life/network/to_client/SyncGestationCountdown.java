package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncGestationCountdown(int v, int playerID) implements CustomPacketPayload {

    public static final Type<SyncGestationCountdown> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_gestation_countdown"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncGestationCountdown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncGestationCountdown::v,
            ByteBufCodecs.INT, SyncGestationCountdown::playerID,
            SyncGestationCountdown::new
    );

    public static void handleClient(final SyncGestationCountdown packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.PAIRING_DATA).setGestationCountdown(packet.v());
            }
        });
    }
}
