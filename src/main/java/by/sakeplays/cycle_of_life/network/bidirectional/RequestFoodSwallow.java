package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.HeldFoodData;
import by.sakeplays.cycle_of_life.entity.util.Diet;
import by.sakeplays.cycle_of_life.network.to_client.SyncFoodLevel;
import by.sakeplays.cycle_of_life.network.to_client.SyncHeldFoodType;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestFoodSwallow(int target) implements CustomPacketPayload {

    public static final Type<RequestFoodSwallow> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_food_swallow"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestFoodSwallow> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestFoodSwallow::target,
            RequestFoodSwallow::new
    );

    public static void handleClient(final RequestFoodSwallow packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(DataAttachments.SWALLOW_ANIM_FLAG, true);
        });
    }

    public static void handleServer(final RequestFoodSwallow packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            HeldFoodData heldFoodData = context.player().getData(DataAttachments.HELD_FOOD_DATA);
            DinoData dinoData = context.player().getData(DataAttachments.DINO_DATA);

            if (context.player().getData(DataAttachments.EATING_TIME) > 0) {
                return;
            }

            if (heldFoodData.getHeldFood() == DinosaurFood.FOOD_NONE || heldFoodData.getFoodWeight() <= 0f) {
                return;
            }

            if (dinoData.getFoodLevel() > 0.99f) {
                return;
            }

            if (heldFoodData.getHeldFood().getDiet() != Util.getDino(context.player()).getDiet()) {
                context.player().sendSystemMessage(Component.literal("This food doesn't match your diet type."));
                return;
            }

            if (heldFoodData.getFoodWeight() > dinoData.getWeight()/19f) {
                context.player().sendSystemMessage(Component.literal("Can't swallow this much food."));
                return;
            }


            context.player().setData(DataAttachments.EATING_TIME, Util.getDino(context.player()).getEatingTime());

            float foodWeight = heldFoodData.getFoodWeight();
            float stomachSize = (float) (Math.pow(dinoData.getWeight(), 0.95f)) * 0.25f;

            float ratio = foodWeight/stomachSize;

            dinoData.setFoodLevel(Math.min(1f, dinoData.getFoodLevel() + ratio));

            heldFoodData.setHeldFoodItem(DinosaurFood.FOOD_NONE);
            heldFoodData.setFoodWeight(0f);

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SyncHeldFoodType(context.player().getId(), DinosaurFood.FOOD_NONE.toString()));
            PacketDistributor.sendToPlayersTrackingEntity(context.player(), new SyncHeldFoodType(context.player().getId(), DinosaurFood.FOOD_NONE.toString()));
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SyncFoodLevel(context.player().getId(), dinoData.getFoodLevel()));
            PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet);
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), packet);

        });
    }
}
