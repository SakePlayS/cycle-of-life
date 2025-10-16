package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncHeldFoodType(int playerId, String foodType) implements CustomPacketPayload {

    public static final Type<SyncHeldFoodType> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_held_food_type"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncHeldFoodType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncHeldFoodType::playerId,
            ByteBufCodecs.STRING_UTF8, SyncHeldFoodType::foodType,
            SyncHeldFoodType::new
    );

    public static void handleClient(final SyncHeldFoodType packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.getData(DataAttachments.HELD_FOOD_DATA).setHeldFoodItem(DinosaurFood.fromString(packet.foodType()));

            }
        });
    }
}
