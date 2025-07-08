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

public record SyncIsSliding(boolean sliding, int playerID) implements CustomPacketPayload {

    public static final Type<SyncIsSliding> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_is_sliding"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncIsSliding> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncIsSliding::sliding,
            ByteBufCodecs.INT, SyncIsSliding::playerID,
            SyncIsSliding::new
    );

    public static void handleClient(final SyncIsSliding packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setSliding(packet.sliding());
            }
        });
    }

    public static void handleServer(final SyncIsSliding packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setSliding(packet.sliding());
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
