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

import java.util.UUID;

public record SyncStoredEggs(int v, int playerID) implements CustomPacketPayload {

    public static final Type<SyncStoredEggs> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_stored_eggs"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncStoredEggs> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncStoredEggs::v,
            ByteBufCodecs.INT, SyncStoredEggs::playerID,
            SyncStoredEggs::new
    );

    public static void handleClient(final SyncStoredEggs packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.PAIRING_DATA).setStoredEggs(packet.v());
            }
        });
    }
}
