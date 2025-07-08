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

public record SyncWaterLevel(int playerId, float water_level) implements CustomPacketPayload {

    public static final Type<SyncWaterLevel> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_water_level"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncWaterLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncWaterLevel::playerId,
            ByteBufCodecs.FLOAT, SyncWaterLevel::water_level,
            SyncWaterLevel::new
    );

    public static void handleClient(final SyncWaterLevel packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.getData(DataAttachments.DINO_DATA).setWaterLevel(packet.water_level());

            }
        });
    }
}
