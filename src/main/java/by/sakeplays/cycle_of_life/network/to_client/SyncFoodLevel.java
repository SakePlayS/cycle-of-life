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

public record SyncFoodLevel(int playerId, float foodLevel) implements CustomPacketPayload {

    public static final Type<SyncFoodLevel> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_food_level"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncFoodLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncFoodLevel::playerId,
            ByteBufCodecs.FLOAT, SyncFoodLevel::foodLevel,
            SyncFoodLevel::new
    );

    public static void handleClient(final SyncFoodLevel packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.getData(DataAttachments.DINO_DATA).setFoodLevel(packet.foodLevel());

            }
        });
    }
}
