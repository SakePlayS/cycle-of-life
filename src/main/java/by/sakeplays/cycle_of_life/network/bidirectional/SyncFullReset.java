package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.event.client.OnRenderPlayerEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFullReset(int playerID) implements CustomPacketPayload {

    public static final Type<SyncFullReset> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_full_reset"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncFullReset> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncFullReset::playerID,
            SyncFullReset::new
    );

    public static void handleClient(final SyncFullReset packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).fullReset();
                player.getData(DataAttachments.PAIRING_DATA).reset(true);
                if (OnRenderPlayerEvent.PLAYER_DINOS.containsKey(packet.playerID)) OnRenderPlayerEvent.PLAYER_DINOS.remove(packet.playerID);
            }
        });
    }

    public static void handleServer(final SyncFullReset packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).fullReset();
                player.getData(DataAttachments.PAIRING_DATA).reset(true);
            }
        }).thenRun(() -> PacketDistributor.sendToAllPlayers( packet));
    }
}
