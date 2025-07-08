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

public record SyncInitialized(int playerId, boolean initialized) implements CustomPacketPayload {

    public static final Type<SyncInitialized> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_initialized"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncInitialized> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncInitialized::playerId,
            ByteBufCodecs.BOOL, SyncInitialized::initialized,
            SyncInitialized::new
    );

    public static void handleClient(final SyncInitialized packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.getData(DataAttachments.DINO_DATA).setInitialized(packet.initialized());

            }
        });
    }
}
