package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.network.to_server.RequestNestCreation;
import by.sakeplays.cycle_of_life.network.to_server.SyncPairing;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.client.screen.StatsScreen;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
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
import java.util.UUID;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HandleKeys {

    public static float turnMultiplier = 0f;
    public static float speed = 0;
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
    protected static boolean isGrabbing = false;
    protected static int lastGrabTick = 0;
    protected static float additionalSpeed = 1;
    public static float dz = 0;
    public static float dx = 0;
    private static boolean inertiaLocked = false;
    private static boolean pairMovementLocked = false;
    protected static float angleDiff = 0;



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
        float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);
        float acceleration = Util.getAcceleration(player);
        float drag = 0.15f / (float) Math.pow(dinoData.getWeight() + 1, 0.17d);
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

        float newTurnDegree = turnDegree;
        float turnTransitionSpeed = Math.min(turnSpeed/35 * Mth.RAD_TO_DEG, 1);

        float targetYaw = yRot + additionalTurn;
        angleDiff = Mth.wrapDegrees((float) Math.toDegrees(targetYaw - turnDegree)) * Mth.DEG_TO_RAD;
        float delta = Mth.clamp(angleDiff, -turnSpeed, turnSpeed);

        if (angleDiff > -0.01) turnMultiplier = Math.min(1, turnMultiplier + turnTransitionSpeed);
        if (angleDiff < 0.01) turnMultiplier = Math.max(-1, turnMultiplier - turnTransitionSpeed);

        if (movementKeyDown()) {

            if (shouldMove(player)) {
                handleForwardMovement(maxSpeed, acceleration, drag);
            } else {
                handleDrag(drag);
            }

            dinoData.setMoving(true);
            PacketDistributor.sendToServer(new SyncDinoWalking(true, player.getId()));

            newTurnDegree = handlePlayerRotation(turnDegree, delta, player);

        } else {
            handleDrag(drag);

            dinoData.setMoving(false);
            PacketDistributor.sendToServer(new SyncDinoWalking(false, player.getId()));
        }

        if (angleDiff < 0.01 && angleDiff > -0.01) {
            if (turnMultiplier > 0) turnMultiplier = Math.max(turnMultiplier - turnTransitionSpeed, 0);
            if (turnMultiplier < 0) turnMultiplier = Math.min(turnMultiplier + turnTransitionSpeed, 0);
        }

        player.setData(DataAttachments.TURN_PROGRESS, turnMultiplier);
        PacketDistributor.sendToServer(new SyncTurnProgress(turnMultiplier, player.getId()));


        if (!(isAirborne(player)) && player.getData(DataAttachments.KNOCKDOWN_TIME) < 1) {
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

    private static boolean isAirborne(Player player) {
        return (!player.onGround() && !player.isInWater());
    }


    private static boolean shouldMove(Player player) {
        if (player.getData(DataAttachments.KNOCKDOWN_TIME) <= 0 && canMove) {
            return true;
        }

        return false;
    }

    private static void handleForwardMovement(float maxSpeed, float acceleration, float drag) {
        if (speed <= maxSpeed) {
            speed = Math.min(maxSpeed, speed + acceleration);
        } else {
            handleDrag(drag);
        }
    }

    private static void handleDrag(float drag) {
        speed = Math.max(0, speed - drag);
    }

    private static float handlePlayerRotation(float turnDegree, float delta, Player player) {

        float newTurnDegree = turnDegree + (delta * Math.abs(turnMultiplier));

        if (!(turningLocked || !canMove || KeyMappings.DIRECTIONAL_ATTACK.isDown() || player.getData(DataAttachments.KNOCKDOWN_TIME) > 0)) {
            player.setData(DataAttachments.PLAYER_ROTATION, newTurnDegree);
            PacketDistributor.sendToServer(new SyncPlayerRotation(newTurnDegree, player.getId()));
        } else {
            newTurnDegree = player.getData(DataAttachments.PLAYER_ROTATION);
        }

        return newTurnDegree;
    }


    private static boolean movementKeyDown() {
        return (KeyMappings.FORWARD_MAPPING.isDown() || KeyMappings.BACKWARD_MAPPING.isDown() ||
                KeyMappings.RIGHT_MAPPING.isDown() || KeyMappings.LEFT_MAPPING.isDown());
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

        if (player.getData(DataAttachments.PAIRING_DATA).isPaired()) {
            if (KeyMappings.PLACE_NEST_MAPPING.isDown() && !pairingLocked) {
                pairingLocked = true;
                PacketDistributor.sendToServer(new RequestNestCreation());
                return;
            } else if (!KeyMappings.PLACE_NEST_MAPPING.isDown() && pairingLocked) {
                pairingLocked = false;
                return;
            }
        }

        if (player.getData(DataAttachments.PAIRING_STATE) == 1) pairingTimeOut = 55;
        if (pairingTimeOut > 0) {
            canMove = false;
            turningLocked = true;
            pairMovementLocked = true;
        } else if (pairingTimeOut == 0 && pairMovementLocked) {
            canMove = true;
            turningLocked = false;
            pairMovementLocked = false;
        }

        if (player.getData(DataAttachments.DINO_DATA).getGrowth() <= 0.999f) return;


        if (KeyMappings.PAIR_MAPPING.isDown()) {
            isPairing = true;
            canMove = false;
            turningLocked = true;

            player.setData(DataAttachments.ATTEMPTING_PAIRING, true);
            PacketDistributor.sendToServer(new SyncAttemptingPairing(true, player.getId()));

        } else if (isPairing) {
            isPairing = false;
            turningLocked = false;
            canMove = true;
            pairingLocked = false;

            player.setData(DataAttachments.ATTEMPTING_PAIRING, false);
            PacketDistributor.sendToServer(new SyncAttemptingPairing(false, player.getId()));

            player.getData(DataAttachments.DINO_DATA).setPairingWith(UUID.randomUUID());
            PacketDistributor.sendToServer(new SyncPairing(-1, player.getId()));
        }

        if (isPairing && !pairingLocked) {
            pairingLocked = true; // make sure we check for targets only 1 time when the player holds down the key

            if (player.getData(DataAttachments.PAIRING_DATA).isPaired()) return;

            float dirX = (float) -Math.sin(player.getData(DataAttachments.PLAYER_ROTATION));
            float dirZ = (float) Math.cos(player.getData(DataAttachments.PLAYER_ROTATION));

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

                        int targetID = target.getId();
                        UUID targetUUID = target.getUUID();

                        player.getData(DataAttachments.DINO_DATA).setPairingWith(targetUUID);
                        PacketDistributor.sendToServer(new SyncPairing(targetID, player.getId()));
                        iterations++;

                }
            }
        }
    }


    private static void requestDrinking(Player player) {

        if (ClientHitboxData.getOwnHitboxes().isEmpty()) return;

        AssociatedAABB head = ClientHitboxData.getOwnHitboxes().getFirst();
        Position headPos = ClientHitboxData.getPos(head, true);

        if (KeyMappings.EAT_MAPPING.isDown()) {
            if (!drinkingLocked) {
                drinkingLocked = true;
                PacketDistributor.sendToServer(new RequestDrinking(true, player.getId(), headPos.x(), headPos.z()));
            }
        } else {
            if (drinkingLocked) {
                drinkingLocked = false;
                PacketDistributor.sendToServer(new RequestDrinking(false, player.getId(), headPos.x(), headPos.z()));
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
