package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncDinoSprint;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncDinoWalking;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnDegree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HandleKeys {

    public static float turnMultiplier = 0f;
    private static float speed = 0;
    private static float maxSpeed = 0;
    private static float trueMaxSpeed = 0;
    private static int tick = 0;


    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        handleSprint(player);
        handleTurning(player);
        handleForwardMovement(player);

    }


    private static void handleSprint(Player player) {
        if (KeyMappingsEvent.SPRINT_MAPPING.isDown()) {
            player.getData(DataAttachments.DINO_DATA).setSprinting(true);
            PacketDistributor.sendToServer(new SyncDinoSprint(true, player.getId()));
        } else {
            player.getData(DataAttachments.DINO_DATA).setSprinting(false);
            PacketDistributor.sendToServer(new SyncDinoSprint(false, player.getId()));
        }
    }

    private static void handleTurning(Player player) {

        float turnSpeed = Util.getTurnSpeed(player) * Mth.DEG_TO_RAD;;

        if (KeyMappingsEvent.LEFT_MAPPING.isDown()) {
            turnMultiplier = turnMultiplier + turnSpeed;
            turnMultiplier = Math.min(1, turnMultiplier + turnSpeed);

            float desiredTurnDegree = player.getData(DataAttachments.DINO_DATA).getTurnDegree() + turnSpeed * Util.getTurnPenalty(player) * Math.abs(turnMultiplier);


            player.getData(DataAttachments.DINO_DATA).setTurnDegree(desiredTurnDegree);
            PacketDistributor.sendToServer(new SyncTurnDegree(desiredTurnDegree, player.getId()));
        }

        if (KeyMappingsEvent.RIGHT_MAPPING.isDown()) {
            turnMultiplier = turnMultiplier - turnSpeed;
            turnMultiplier = Math.max(-1, turnMultiplier - turnSpeed);


            float desiredTurnDegree = player.getData(DataAttachments.DINO_DATA).getTurnDegree() - turnSpeed * Util.getTurnPenalty(player) * Math.abs(turnMultiplier);

            player.getData(DataAttachments.DINO_DATA).setTurnDegree(desiredTurnDegree);
            PacketDistributor.sendToServer(new SyncTurnDegree(desiredTurnDegree, player.getId()));
        }

        if (!KeyMappingsEvent.RIGHT_MAPPING.isDown() && !KeyMappingsEvent.LEFT_MAPPING.isDown()) {
            if (turnMultiplier > 0) {
                turnMultiplier = Math.max(0, turnMultiplier - turnSpeed);
            } else {
                turnMultiplier = Math.min(0, turnMultiplier + turnSpeed);
            }
        }
    }

    private static void handleForwardMovement(Player player) {
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        float turnDegree = dinoData.getTurnDegree();
        float walkSpeed = Util.getWalkSpeed(player);
        float sprintSpeed = Util.getSprintSpeed(player);
        float acceleration = Util.getAcceleration(player);
        float swimSpeed = Util.getSwimSpeed(player);
        float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();

        if (player.onGround() || player.isInWater()) {
            if (dinoData.isSprinting()) {
                maxSpeed = sprintSpeed;
            } else {
                if (maxSpeed > walkSpeed) {
                    maxSpeed = maxSpeed - acceleration * 1.5f;
                } else {
                    maxSpeed = walkSpeed;
                }
            }
        }

        maxSpeed = maxSpeed * Mth.lerp(growth, 0.1f, 1f);

        if (player.onGround() || player.isInWater()) {
            if (KeyMappingsEvent.FORWARD_MAPPING.isDown()) {
                dinoData.setMoving(true);
                PacketDistributor.sendToServer(new SyncDinoWalking(true, player.getId()));

                speed = Math.min(maxSpeed, speed + acceleration);
            } else {
                dinoData.setMoving(false);
                PacketDistributor.sendToServer(new SyncDinoWalking(false, player.getId()));

                speed = Math.max(0, speed - acceleration * 1.5f);
            }
        } else {
            speed = speed * 0.985f;
        }




        float dx = (float) Math.sin(turnDegree);
        float dz = (float) Math.cos(turnDegree);

        player.setDeltaMovement(dx * speed, player.getDeltaMovement().y, dz * speed);

    }


}
