package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationType;
import by.sakeplays.cycle_of_life.network.ModCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncAdaptation(AdaptationType adaptation, float progress, int level,
                             int playerID, boolean isUpgraded) implements CustomPacketPayload {

    public static final Type<SyncAdaptation> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_adaptation"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncAdaptation> STREAM_CODEC = StreamCodec.composite(
            ModCodecs.enumCodec(AdaptationType.class), SyncAdaptation::adaptation,
            ByteBufCodecs.FLOAT, SyncAdaptation::progress,
            ByteBufCodecs.INT, SyncAdaptation::level,
            ByteBufCodecs.INT, SyncAdaptation::playerID,
            ByteBufCodecs.BOOL, SyncAdaptation::isUpgraded,
            SyncAdaptation::new
    );

    public static void handleClient(final SyncAdaptation packet, final IPayloadContext context) {
        context.enqueueWork(() -> {

            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                Adaptation data = player.getData(DataAttachments.ADAPTATION_DATA).getAdaptation(packet.adaptation());

                if (data == null) {
                    CycleOfLife.LOGGER.warn("Error while handling cycle_of_life:sync_adaptation packet: " +
                            "unrecognized adaptation type '" + packet.adaptation + "', skipping.");
                    return;
                }

                data.setProgress(packet.progress());
                data.setLevel(packet.level);
                data.setUpgraded(packet.isUpgraded);
            }
        });
    }
}
