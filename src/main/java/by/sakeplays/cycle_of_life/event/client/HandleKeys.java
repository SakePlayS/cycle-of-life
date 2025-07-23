package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.client.screen.StatsScreen;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusBite;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusDoubleSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestGrabFood;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HandleKeys {

    public static float turnMultiplier = 0f;
    protected static float speed = 0;
    public static int attackTimer = 0;
    public static int attackTimeout = 0;
    public static boolean turningLocked = false;
    protected static boolean canMove = true;
    protected static boolean restingPressed = false;
    protected static boolean isPairing = false;
    protected static int restingTimerOut = 0;
    protected static int restingTimerIn = 0;
    protected static int pairingTimeOut = 0;
    protected static boolean pairingLocked = false;
    protected static boolean drinkingLocked = false;
    public static float directionalAttackDesiredAngle = 0;
    protected static boolean isGrabbing = false;
    protected static int lastGrabTick = 0;
    protected static float additionalSpeed = 1;
    protected static float dz = 0;
    protected static float dx = 0;



    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        attackTimer--;
        attackTimeout--;

        handleAttacks(player);

        handleSprint(player);
        handleMovement(player);

        handleResting(player);
        handlePairing(player);
        requestDrinking(player);
        openCharacterInfo();
        handleGrabbing(player);

        boolean sliding = (!player.getData(DataAttachments.DINO_DATA).isMoving() &&
                (player.getDeltaMovement().x() != 0 || player.getDeltaMovement().z() != 0));

        player.getData(DataAttachments.DINO_DATA).setSliding(sliding);
        PacketDistributor.sendToServer(new SyncIsSliding(sliding, player.getId()));


    }

    private static void handleGrabbing(Player player) {

        if (!canMove) return;
        if (turningLocked) return;
        if (player.tickCount - lastGrabTick < 10) return;

        if (KeyMappings.GRAB_MAPPING.isDown()) {
            if (!isGrabbing) {
                isGrabbing = true;
                PacketDistributor.sendToServer(new RequestGrabFood());
                lastGrabTick = player.tickCount;
            }
        } else {
            isGrabbing = false;
        }
    }



    private static void handleSprint(Player player) {

        if (KeyMappings.SPRINT_MAPPING.isDown()) {

            if (player.getData(DataAttachments.DINO_DATA).getStamina() <= 0.001f) {
                player.getData(DataAttachments.DINO_DATA).setSprinting(false);
                PacketDistributor.sendToServer(new SyncDinoSprint(false, player.getId()));
            } else {
                player.getData(DataAttachments.DINO_DATA).setSprinting(true);
                PacketDistributor.sendToServer(new SyncDinoSprint(true, player.getId()));
            }
        } else {
            player.getData(DataAttachments.DINO_DATA).setSprinting(false);
            PacketDistributor.sendToServer(new SyncDinoSprint(false, player.getId()));
        }
    }


    private static void handleMovement(Player player) {
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        float turnSpeed = Util.getTurnSpeed(player) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_TURN);
        float acceleration = Util.getAcceleration(player);
        float drag = (float) Math.pow(dinoData.getWeight() + 1, 0.2d);
        float yRot = player.getYRot() * Mth.DEG_TO_RAD;
        float maxSpeed = Util.calculateSpeed(player) * additionalSpeed;

        float additionalTurn = 0;


        if (!player.onGround() && !player.isInWater()) {
            turnSpeed *= 0.1f;
            speed *= 0.985f;
        }

        if (player.isInWater()) {
            drag *= 2f;
            acceleration *= 0.5f;
            turnSpeed *= 0.75f;

        }

        if (KeyMappings.LEFT_MAPPING.isDown() && KeyMappings.BACKWARD_MAPPING.isDown()) {
            additionalTurn = -(2.35619f);
        } else if (KeyMappings.RIGHT_MAPPING.isDown() && KeyMappings.BACKWARD_MAPPING.isDown()) {
            additionalTurn = (2.35619f);
        } else if (KeyMappings.LEFT_MAPPING.isDown() && KeyMappings.FORWARD_MAPPING.isDown()) {
            additionalTurn = (float) -(Math.PI / 4f);
        } else if (KeyMappings.RIGHT_MAPPING.isDown() && KeyMappings.FORWARD_MAPPING.isDown()) {
            additionalTurn = (float) (Math.PI / 4f);
        } else if (KeyMappings.LEFT_MAPPING.isDown()) {
            additionalTurn = (float) -(Math.PI / 2f);
        } else if (KeyMappings.RIGHT_MAPPING.isDown()) {
            additionalTurn = (float) (Math.PI / 2f);
        } else if (KeyMappings.BACKWARD_MAPPING.isDown()) {
            additionalTurn = (float) (Math.PI);
        }

        player.setData(DataAttachments.ADDITIONAL_TURN, additionalTurn);
        PacketDistributor.sendToServer(new SyncAdditionalTurn(player.getId(), additionalTurn));

        float angleDiff = 0;
        float newTurnDegree = turnDegree;
        float turnTransitionSpeed = Math.min(turnSpeed/35 * Mth.RAD_TO_DEG, 1);

        if (KeyMappings.FORWARD_MAPPING.isDown() || KeyMappings.BACKWARD_MAPPING.isDown() ||
                KeyMappings.RIGHT_MAPPING.isDown() || KeyMappings.LEFT_MAPPING.isDown()) {

            float targetYaw = yRot + additionalTurn;
            angleDiff = Mth.wrapDegrees((float) Math.toDegrees(targetYaw - turnDegree)) * Mth.DEG_TO_RAD;
            float delta = Mth.clamp(angleDiff, -turnSpeed, turnSpeed);

            if (angleDiff > -0.01) turnMultiplier = Math.min(1, turnMultiplier + turnTransitionSpeed);
            if (angleDiff < 0.01) turnMultiplier = Math.max(-1, turnMultiplier - turnTransitionSpeed);

            newTurnDegree = turnDegree + (delta * Math.abs(turnMultiplier));

            if (!(turningLocked || !canMove || KeyMappings.DIRECTIONAL_ATTACK.isDown())) {
                player.setData(DataAttachments.PLAYER_TURN, newTurnDegree);
                PacketDistributor.sendToServer(new SyncTurnDegree(newTurnDegree, player.getId()));
            } else {
                newTurnDegree = player.getData(DataAttachments.PLAYER_TURN);
            }

            dinoData.setMoving(true);
            PacketDistributor.sendToServer(new SyncDinoWalking(true, player.getId()));

            if (speed < maxSpeed && !(!player.onGround() && !player.isInWater())) {
                speed += acceleration;
            }

        } else {

            dinoData.setMoving(false);
            PacketDistributor.sendToServer(new SyncDinoWalking(false, player.getId()));

            if (!(!player.onGround() && !player.isInWater())) speed -= (0.1f / drag);
            if (speed < 0) speed = 0;

        }

        if (angleDiff < 0.01 && angleDiff > -0.01) {
            if (turnMultiplier > 0) turnMultiplier = Math.max(turnMultiplier - turnTransitionSpeed, 0);
            if (turnMultiplier < 0) turnMultiplier = Math.min(turnMultiplier + turnTransitionSpeed, 0);
        }

        player.setData(DataAttachments.TURN_PROGRESS, turnMultiplier);
        PacketDistributor.sendToServer(new SyncTurnProgress(turnMultiplier, player.getId()));


        if (speed > maxSpeed  && !(!player.onGround() && !player.isInWater())) {
            speed -= (Util.calculateMaxSpeed(player)/110f / drag);
        }

        if (!(!player.onGround() && !player.isInWater())) {
            dx = (float) -Math.sin(newTurnDegree);
            dz = (float) Math.cos(newTurnDegree);
        }

        player.setDeltaMovement(dx * speed, player.getDeltaMovement().y, dz * speed);

        float speed = new Vec2((float) player.getDeltaMovement().x(),
                (float) player.getDeltaMovement().z()).length();

        player.setData(DataAttachments.SPEED, speed);
        PacketDistributor.sendToServer(new SyncSpeed(player.getId(), speed));

        additionalSpeed = 1;
    }



    private static void handleAttacks(Player player) {
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        switch (data.getSelectedDinosaur()) {
            case 1 -> {
                Attacks.pachycephalosaurus(player);
                break;
            }
            case 2 -> {
                Attacks.deinonychus(player);
                break;
            }
        }
    }

    private static void handleResting(Player player) {
        if (KeyMappings.REST_MAPPING.isDown() ) {

            if (KeyMappings.PAIR_MAPPING.isDown()) return;

            if (!restingPressed && restingTimerOut <= 0) {
                restingPressed = true;
                canMove = false;
                if (player.getData(DataAttachments.RESTING_STATE) == 0 && restingTimerIn == 0) {
                    restingTimerIn = 21;

                    player.setData(DataAttachments.RESTING_STATE, 1);
                    PacketDistributor.sendToServer(new SyncRestingState(1, player.getId()));

                }

                if (player.getData(DataAttachments.RESTING_STATE) == 2) {
                    player.setData(DataAttachments.RESTING_STATE, 3);
                    PacketDistributor.sendToServer(new SyncRestingState(3, player.getId()));

                    restingTimerOut = 20;
                }
            }
        } else {
            restingPressed = false;
        }

        if (restingTimerOut > 0) {
            restingTimerOut--;
        }

        if (restingTimerIn > 0) {
            restingTimerIn--;
        }

        if (restingTimerOut == 1) {
            player.setData(DataAttachments.RESTING_STATE, 0);
            PacketDistributor.sendToServer(new SyncRestingState(0, player.getId()));
            canMove = true;

        }

        if (restingTimerIn == 1) {
            player.setData(DataAttachments.RESTING_STATE, 2);
            PacketDistributor.sendToServer(new SyncRestingState(2, player.getId()));

        }
    }

    private static void handlePairing(Player player) {

        pairingTimeOut--;

        if (player.getData(DataAttachments.PAIRING_STATE) == 1) pairingTimeOut = 55;
        if (pairingTimeOut > 0) {
            canMove = false;
            turningLocked = true;
        } else if (pairingTimeOut == 0) {
            canMove = true;
            turningLocked = false;
        }

        if (player.getData(DataAttachments.DINO_DATA).getGrowth() <= 0.99f) return;
        if (player.getData(DataAttachments.DINO_DATA).isPaired()) return;

        if (KeyMappings.PAIR_MAPPING.isDown()) {
            isPairing = true;
            canMove = false;
            turningLocked = true;

            player.setData(DataAttachments.ATTEMPTING_PAIRING, true);
            PacketDistributor.sendToServer(new SyncAttemptingPairing(true, player.getId()));

        } else if (isPairing){
            isPairing = false;
            turningLocked = false;
            canMove = true;
            pairingLocked = false;

            player.setData(DataAttachments.ATTEMPTING_PAIRING, false);
            PacketDistributor.sendToServer(new SyncAttemptingPairing(false, player.getId()));

            player.getData(DataAttachments.DINO_DATA).setPairingWith(0);
            PacketDistributor.sendToServer(new SyncPairingWith(0, player.getId()));
        }

        if (isPairing && !pairingLocked) {
            pairingLocked = true; // make sure we check for targets only 1 time when the player holds down the key


            float dirX = (float) -Math.sin(player.getData(DataAttachments.PLAYER_TURN));
            float dirZ = (float) Math.cos(player.getData(DataAttachments.PLAYER_TURN));

            double x = player.getX() + dirX * 2.5;
            double y = player.getY();
            double z = player.getZ() + dirZ * 2.5;
            double size = 2.5f;

            AABB hitbox = new AABB(
                    x - size, y, z - size,
                    x + size, y + 1.2d, z + size
            );

            int iterations = 0;

            List<Player> possibleTargets = player.level().getEntities(EntityTypeTest.forClass(Player.class), hitbox, e -> true);


            for (Player target : possibleTargets) {

                if (target != player && iterations <= 0) {

                    if (target.getData(DataAttachments.DINO_DATA).getGrowth() > 0.99f &&
                            (target.getData(DataAttachments.DINO_DATA).isMale() != player.getData(DataAttachments.DINO_DATA).isMale())) {


                        int targetID = target.getId();

                        player.getData(DataAttachments.DINO_DATA).setPairingWith(targetID);
                        PacketDistributor.sendToServer(new SyncPairingWith(targetID, player.getId()));

                        iterations++;
                    }
                }
            }
        }
    }

    private static void attemptToPlaceNest(Player player) {

    }



    private static void requestDrinking(Player player) {
        if (KeyMappings.EAT_MAPPING.isDown()) {
            if (!drinkingLocked) {
                drinkingLocked = true;
                PacketDistributor.sendToServer(new RequestDrinking(true, player.getId()));
            }
        } else {
            if (drinkingLocked) {
                drinkingLocked = false;
                PacketDistributor.sendToServer(new RequestDrinking(false, player.getId()));
            }
        }
    }

    private static void openCharacterInfo() {
        if (KeyMappings.CHARACTER_MAPPING.isDown()) {
            if (!(Minecraft.getInstance().screen instanceof StatsScreen)) {
                Minecraft.getInstance().setScreen(new StatsScreen(Component.literal("Stats")));
            }
        }
    }
}
