package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.block.HerbivoreFoodBlockEntity;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.HeldFoodData;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.util.Diet;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.ModEntities;
import by.sakeplays.cycle_of_life.entity.FoodEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestGrabFood(double headX, double headY, double headZ) implements CustomPacketPayload {

    public static final Type<RequestGrabFood> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_grab_food"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestGrabFood> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, RequestGrabFood::headX,
            ByteBufCodecs.DOUBLE, RequestGrabFood::headY,
            ByteBufCodecs.DOUBLE, RequestGrabFood::headZ,
            RequestGrabFood::new
            );

    public static void handleServer(final RequestGrabFood packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Util.getDino(context.player()) == Dinosaurs.NONE) return;
            Player player = context.player();
            Level level = player.level();
            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
            HeldFoodData heldFoodData = player.getData(DataAttachments.HELD_FOOD_DATA);

            if (Util.getDino(player).getDiet() == Diet.CARNIVORE || Util.getDino(player).getDiet() == Diet.OMNIVORE) {
                handleCarnivore(packet, level, heldFoodData, dinoData, player);
                return;
            }

            if (Util.getDino(player).getDiet() == Diet.HERBIVORE || Util.getDino(player).getDiet() == Diet.OMNIVORE) {
                handleHerbivore(packet, level, heldFoodData, dinoData, player);
            }
        });
    }


    private static void handleHerbivore(RequestGrabFood packet, Level level, HeldFoodData heldFoodData, DinoData dinoData, Player player) {
        if (heldFoodData.getHeldFood() == DinosaurFood.FOOD_NONE) {


            double yLimit = packet.headY() + 1f;
            for (double currentY = player.getY(); currentY <= yLimit; currentY = currentY + 0.25f) {

                BlockEntity be = level.getBlockEntity(BlockPos.containing(packet.headX, currentY, packet.headZ));

                if (be != null) {
                    if (be instanceof HerbivoreFoodBlockEntity blockEntity) {
                        boolean pieceTaken = blockEntity.takePiece(player, dinoData.getWeight() / Util.getDino(player).getGrabPartition());
                        if (pieceTaken) return;
                    }
                }
            }

            takePieceFromFood(packet, level, player, dinoData, heldFoodData, Diet.HERBIVORE);

        } else {
            dropFood(player, packet, level);
        }
    }

    private static void handleCarnivore(RequestGrabFood packet, Level level, HeldFoodData heldFoodData, DinoData dinoData, Player player) {
        if (heldFoodData.getHeldFood() == DinosaurFood.FOOD_NONE) {
            List<DinosaurEntity> dinosaurEntities = level.getEntitiesOfClass(DinosaurEntity.class,
                    new AABB(packet.headX() - 0.3f, player.getY() - 0.3f, packet.headZ() - 0.3f,
                            packet.headX() + 0.3f, player.getY() + 0.3f + player.getBbHeight(), packet.headZ() + 0.3f).inflate(player.getBbWidth()));

            for (DinosaurEntity dinosaurEntity : dinosaurEntities) {

                if (!dinosaurEntity.isCorpse()) continue;

                heldFoodData.setHeldFoodItem(dinosaurEntity.getMeatType());
                heldFoodData.setFoodWeight(Math.min(dinosaurEntity.getRemainingFood(), dinoData.getWeight()/Util.getDino(player).getGrabPartition()));

                dinosaurEntity.setRemainingFood(dinosaurEntity.getRemainingFood() - dinoData.getWeight()/Util.getDino(player).getGrabPartition());
                return;
            }

            takePieceFromFood(packet, level, player, dinoData, heldFoodData, Diet.CARNIVORE);

        } else {
            dropFood(player, packet, level);
        }
    }

    private static void takePieceFromFood(RequestGrabFood packet, Level level, Player player, DinoData dinoData, HeldFoodData heldFoodData, Diet requiredDiet) {
        List<FoodEntity> foodEntities = level.getEntitiesOfClass(FoodEntity.class,
                new AABB(packet.headX() - 0.3f, player.getY() - 0.3f, packet.headZ() - 0.3f,
                        packet.headX() + 0.3f, player.getY() + 0.3f, packet.headZ() + 0.3f).inflate(player.getBbWidth()/2));

        for (FoodEntity food : foodEntities) {

            if (food.getFoodType().getDiet() != requiredDiet) continue;

            heldFoodData.setHeldFoodItem(food.getFoodType());
            heldFoodData.setFoodWeight(Math.min(food.getRemainingFood(), dinoData.getWeight()/Util.getDino(player).getGrabPartition()));

            food.setRemainingFood(food.getRemainingFood() - dinoData.getWeight()/Util.getDino(player).getGrabPartition());
            return;
        }
    }

    private static void dropFood(Player player, RequestGrabFood packet, Level level) {
        HeldFoodData heldFoodData = player.getData(DataAttachments.HELD_FOOD_DATA);

        FoodEntity foodEntity = ModEntities.FOOD_ENTITY.get().create(player.level());
        if (foodEntity != null) {
            foodEntity.setFoodType(heldFoodData.getHeldFood());
            foodEntity.setRemainingFood(heldFoodData.getFoodWeight());

            heldFoodData.setFoodWeight(0);
            heldFoodData.setHeldFoodItem(DinosaurFood.FOOD_NONE);

            foodEntity.setPos(packet.headX(), packet.headY(), packet.headZ());

            foodEntity.setDeltaMovement(player.getX() - player.xOld, player.getY() - player.yOld, player.getZ() - player.zOld);

            level.addFreshEntity(foodEntity);
        }
    }
}
