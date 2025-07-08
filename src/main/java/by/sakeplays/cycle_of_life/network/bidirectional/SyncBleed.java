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

public record SyncBleed(int playerId, float bleed) implements CustomPacketPayload {

    public static final Type<SyncBleed> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_bleed"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncBleed> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncBleed::playerId,
            ByteBufCodecs.FLOAT, SyncBleed::bleed,
            SyncBleed::new
    );

    public static void handleClient(final SyncBleed packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.getData(DataAttachments.DINO_DATA).setBleed(packet.bleed());

            }
        });
    }

    public static void handleServer(final SyncBleed packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setBleed(packet.bleed());
            }
        }).thenRun(() -> PacketDistributor.sendToAllPlayers(packet));
    }
}
