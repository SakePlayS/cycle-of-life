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

public record SyncMateUUID(String v, int playerID) implements CustomPacketPayload {

    public static final Type<SyncMateUUID> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_mate_uuid"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncMateUUID> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SyncMateUUID::v,
            ByteBufCodecs.INT, SyncMateUUID::playerID,
            SyncMateUUID::new
    );

    public static void handleClient(final SyncMateUUID packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.PAIRING_DATA).setMateUUID(UUID.fromString(packet.v()));
            }
        });
    }
}
