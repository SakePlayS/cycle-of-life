package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.bidirectional.RequestFoodSwallow;
import by.sakeplays.cycle_of_life.network.to_server.RequestNestCreation;
import by.sakeplays.cycle_of_life.network.to_server.SyncPairing;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.client.screen.StatsScreen;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_server.RequestGrabFood;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
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
    private static boolean pairMovementLocked = false;
    protected static float angleDiff = 0;
    private static int airborneTime = 0;
    public static int cameraMode = 0;
    private static float turningRate = 0;



    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || player.getData(DataAttachments.DINO_DATA).isInHumanMode()) return;

        attackTimer--;
        attackTimeout--;

        if (KeyMappings.TOGGLE_CAMERA_MODE.consumeClick()) {
           cameraMode = (cameraMode + 1) % 3;
        }

        handleTakeoff(player);

        AttackDispatcher.tick();
        handleSprint(player);
        handleMovement(player);
        handleJump(player);

        handleResting(player);
        handlePairing(player);

        if (player.getData(DataAttachments.HELD_FOOD_DATA).getHeldFood() == DinosaurFood.FOOD_NONE) {
            handleDrinking(player);
        } else {
            handleEating(player);
        }
        openCharacterInfo();
        handleGrabbing(player);

        airborneTime = isAirborne(player) ? airborneTime + 1 : 0;

    }

    private static void actuallyJump(Player player) {
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float baseJumpStrength = Util.getDino(player).getJumpStrength();

        float dinoJumpStrength = baseJumpStrength * Util.getDino(player).getGrowthCurve().calculate(data.getGrowth(), GrowthCurveStat.JUMP_STRENGTH);

        if (dinoJumpStrength > 0.32f && player.getData(DataAttachments.KNOCKDOWN_TIME) < 0 && !data.isLayingEggs()) {
            player.setDeltaMovement(
                    player.getDeltaMovement().x,
                    player.getDeltaMovement().y + dinoJumpStrength,
                    player.getDeltaMovement().z
            );

            player.setData(DataAttachments.JUMP_ANIM_FLAG, true);
            PacketDistributor.sendToServer(new SendJumpAnimFlag(player.getId()));

        }
    }

    private static boolean oldWindup = false;
    private static int jumpWindupTicks = 0;
    private static void handleJump(Player player) {

        if (isAirborne(player)) jumpWindupTicks = 0;
        if ((!isAirborne(player) && Minecraft.getInstance().options.keyJump.isDown()) || jumpWindupTicks > 0) jumpWindupTicks++;


        player.setData(DataAttachments.JUMP_WINDUP, jumpWindupTicks > 0);

        if (player.getData(DataAttachments.JUMP_WINDUP) != oldWindup) PacketDistributor.sendToServer(new SyncJumpWindup(player.getData(DataAttachments.JUMP_WINDUP), player.getId()));

        if (!Minecraft.getInstance().options.keyJump.isDown() && jumpWindupTicks > 5) {
            jumpWindupTicks = 0;

            actuallyJump(player);
        }

        oldWindup = player.getData(DataAttachments.JUMP_WINDUP);
    }

    private static void handleGrabbing(Player player) {

        if (!shouldMove(player)) return;

        if (KeyMappings.GRAB_MAPPING.isDown()) {
            if (!isGrabbing) {
                isGrabbing = true;

                if (!ClientHitboxData.getOwnHitboxes().isEmpty()) {
                    AssociatedAABB aabb = null;

                    for (AssociatedAABB hb : ClientHitboxData.getOwnHitboxes()) {
                        if (hb.getType() == HitboxType.HEAD) {
                            aabb = hb;
                            break;
                        }
                    }

                    if (aabb != null) {

                        Position pos = ClientHitboxData.getPos(aabb, true);
                        PacketDistributor.sendToServer(new RequestGrabFood(pos.x(), pos.y(), pos.z()));
                    }
                }
            }
        } else {
            isGrabbing = false;
        }
    }

    private static float sprintBonus = 0;

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

    enum Axis{X, Y, Z}
    private static float xBlockedFactor = 1f;
    private static float zBlockedFactor = 1f;
    private static float maxSpeedPenalty = 1;

    private static void handleMovement(Player player) {

        if (Float.isNaN(xBlockedFactor)) xBlockedFactor = 1f;
        if (Float.isNaN(zBlockedFactor)) zBlockedFactor = 1f;

        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        if (dinoData.isFlying()) {

            handleFlight(player);
            return;
        }

        float turnSpeed = Util.getTurnSpeed(player) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);
        float acceleration = Util.getAcceleration(player);
        float drag = 0.15f / (float) Math.pow(dinoData.getWeight() + 1, 0.17d);
        float yRot = player.getYRot() * Mth.DEG_TO_RAD;
        float maxSpeed = Util.calculateSpeed(player) * additionalSpeed;

        float additionalTurn = 0;
        float newTurnDegree = turnDegree;


        if (airborneTime >= 3) {
            turnSpeed *= 0.2f;
            speed *= 0.985f;
        }

        if (player.isInWater()) {
            drag *= 2f;
            acceleration *= 0.5f;
            turnSpeed *= 0.75f;

        }

        if (movementKeyDown()) {

            if (shouldMove(player)) {
                handleForwardMovement(maxSpeed, acceleration, drag, player);
            } else {
                handleDrag(drag, player);
            }

            dinoData.setMoving(true);
            PacketDistributor.sendToServer(new SyncDinoWalking(true, player.getId()));

            newTurnDegree = handlePlayerRotationAlt(player);

        } else {
            handleDrag(drag, player);

            dinoData.setMoving(false);
            PacketDistributor.sendToServer(new SyncDinoWalking(false, player.getId()));
        }

        if (airborneTime < 3 && player.getData(DataAttachments.KNOCKDOWN_TIME) < 1) {
            dx = (float) -Math.sin(newTurnDegree);
            dz = (float) Math.cos(newTurnDegree);
        }

        float recoverySpeed = (acceleration/maxSpeed) * 3f;

        Vec3 desiredMovement = new Vec3(dx * speed, player.getDeltaMovement().y, dz * speed);

        if (isAxisBlocked(player, desiredMovement, Axis.X)) {
            xBlockedFactor = Math.max(0.07f, xBlockedFactor - 0.3f);
        } else {
            if (!isAirborne(player)) xBlockedFactor = Math.min(1f, xBlockedFactor + recoverySpeed);
        }

        if (isAxisBlocked(player, desiredMovement, Axis.Z)) {
            zBlockedFactor = Math.max(0.07f, zBlockedFactor - 0.3f);
        } else {
            if (!isAirborne(player)) zBlockedFactor = Math.min(1, zBlockedFactor + recoverySpeed);
        }

        player.setDeltaMovement(dx * speed * xBlockedFactor, player.getDeltaMovement().y, dz * speed * zBlockedFactor);

        float movementSpeed = new Vec2((float) player.getDeltaMovement().x(),
                (float) player.getDeltaMovement().z()).length();

        player.setData(DataAttachments.SPEED, movementSpeed);
        PacketDistributor.sendToServer(new SyncSpeed(player.getId(), movementSpeed));

        additionalSpeed = 1;
    }


    private static boolean isAxisBlocked(Player player, Vec3 attemptedMove, Axis axis) {
        AABB box = player.getBoundingBox();

        AABB testBox;
        switch (axis) {
            case X -> testBox = box.move(Math.signum(attemptedMove.x) * 0.01f, 0, 0);
            case Y -> testBox = box.move(0, Math.signum(attemptedMove.y) * 0.01f, 0);
            case Z -> testBox = box.move(0, 0, Math.signum(attemptedMove.z) * 0.01f);
            default -> testBox = box;
        }

        boolean collision = !player.level().noCollision(player, testBox);

        if (!collision) return false;

        return !player.level().noCollision(player, testBox.move(0, player.maxUpStep(), 0));
    }

    private static boolean isAirborne(Player player) {
        return (!player.onGround() && !player.isInWater());
    }


    private static boolean shouldMove(Player player) {

        if (player.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return false;
        if (player.getData(DataAttachments.DINO_DATA).isFlying()) return false;
        if (player.getData(DataAttachments.RESTING_STATE) == 2) return false;

        if (player.getData(DataAttachments.KNOCKDOWN_TIME) <= 0 && !player.getData(DataAttachments.DINO_DATA).isFlying()) {
            return true;
        }

        return false;
    }

    private static void handleForwardMovement(float maxSpeed, float acceleration, float drag, Player player) {
        if (speed <= maxSpeed) {
            if (airborneTime >= 3) {
                speed = speed * 0.985f;
            } else {
                speed = Math.min(maxSpeed, speed + acceleration);
            }
        } else {
            handleDrag(drag, player);
        }
    }

    private static void handleDrag(float drag, Player player) {
        if (isAirborne(player)) {
            speed = speed * 0.985f;
        } else {
            speed = Math.max(0, speed - drag);
        }
    }

    private static float handlePlayerRotation(float turnDegree, float delta, Player player) {

        float newTurnDegree = turnDegree + (delta * Math.abs(turnMultiplier));

        if (AttackDispatcher.isAltAttacking) return player.getData(DataAttachments.PLAYER_ROTATION);
        if (!shouldMove(player)) return player.getData(DataAttachments.PLAYER_ROTATION);
        if (KeyMappings.DIRECTIONAL_ATTACK.isDown()) return player.getData(DataAttachments.PLAYER_ROTATION);
        if (player.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return player.getData(DataAttachments.PLAYER_ROTATION);

        player.setData(DataAttachments.PLAYER_ROTATION, newTurnDegree);
        PacketDistributor.sendToServer(new SyncPlayerRotation(newTurnDegree, player.getId()));

        return newTurnDegree;
    }



    private static float handlePlayerRotationAlt(Player player) {
        if (AttackDispatcher.isAltAttacking) return player.getData(DataAttachments.PLAYER_ROTATION);
        if (!shouldMove(player)) return player.getData(DataAttachments.PLAYER_ROTATION);
        if (KeyMappings.DIRECTIONAL_ATTACK.isDown()) return player.getData(DataAttachments.PLAYER_ROTATION);
        if (player.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return player.getData(DataAttachments.PLAYER_ROTATION);

        final float EPS = 0.0001f;

        float turnSpeed = Util.getTurnSpeed(player) * Mth.DEG_TO_RAD;

        float targetYaw = Mth.wrapDegrees(player.getYRot()) * Mth.DEG_TO_RAD;
        if (KeyMappings.RIGHT_MAPPING.isDown()) targetYaw += Mth.HALF_PI;
        if (KeyMappings.LEFT_MAPPING.isDown()) targetYaw -= Mth.HALF_PI;

        float playerRot = player.getData(DataAttachments.PLAYER_ROTATION);

        float delta = targetYaw - (float) Math.atan2(Math.sin(playerRot), Math.cos(playerRot));
        delta = (float) Math.atan2(Math.sin(delta), Math.cos(delta));

        if (Math.abs(delta) > EPS) {
            if (delta > 0) {
                turningRate = Math.min(turnSpeed, turningRate + turnSpeed * (turningRate < 0 ? 0.4f : 0.2f));
            } else {
                turningRate = Math.max(-turnSpeed, turningRate - turnSpeed * (turningRate > 0 ? 0.4f : 0.2f));
            }
        } else {
            turningRate *= 0.8f - 0.3f * (1.0f - (float)Math.exp(-Math.abs(delta) * 4f));
        }

        float newRot = playerRot + turningRate;

        float newDelta = targetYaw - (float) Math.atan2(Math.sin(newRot), Math.cos(newRot));
        newDelta = (float) Math.atan2(Math.sin(newDelta), Math.cos(newDelta));

        if (Math.signum(delta) != Math.signum(newDelta)) {
            newRot = playerRot + delta;
        }

        if (Math.abs(newRot - playerRot) > EPS) {
            player.setData(DataAttachments.PLAYER_ROTATION, newRot);
            PacketDistributor.sendToServer(new SyncPlayerRotation(newRot, player.getId()));
        }

        return newRot;
    }
    private static boolean movementKeyDown() {
        return (KeyMappings.FORWARD_MAPPING.isDown() || KeyMappings.BACKWARD_MAPPING.isDown() ||
                KeyMappings.RIGHT_MAPPING.isDown() || KeyMappings.LEFT_MAPPING.isDown());
    }


    public static int takeoffHoldTicks;
    private static float xzMomentum = 0.15f;
    private static float yMomentum = 0;
    private static int flyingFor = 0;
    private static float turnSpeed = 0;
    private static boolean airbrakingOld = false;
    private static boolean hitWall = false;

    private static void handleFlight(Player player) {

        DinoData data = player.getData(DataAttachments.DINO_DATA);

        flyingFor++;

        if (flyingFor > 2 && (!isAirborne(player) || !Util.getDino(player).equals(Dinosaurs.PTERANODON))) {

            flyingFor = 0;
            data.setFlying(false);
            PacketDistributor.sendToServer(new SyncFlightState(false, player.getId()));
            xzMomentum = 0.15f;
            return;
        }

        if (KeyMappings.LEFT_MAPPING.isDown()) {
            turnSpeed = Math.max(-0.1f, turnSpeed - 0.012f / (0.25f + Math.max(0.75f, xzMomentum)));
        } else if (KeyMappings.RIGHT_MAPPING.isDown())  {
            turnSpeed = Math.min(0.1f, turnSpeed + 0.012f / (0.25f + Math.max(0.75f, xzMomentum)));
        } else {
            turnSpeed = turnSpeed * 0.91f;
        }

        player.setData(DataAttachments.PLAYER_ROTATION, player.getData(DataAttachments.PLAYER_ROTATION) + turnSpeed);
        PacketDistributor.sendToServer(new SyncPlayerRotation(player.getData(DataAttachments.PLAYER_ROTATION), player.getId()));


        dx = (float) -Math.sin(player.getData(DataAttachments.PLAYER_ROTATION));
        dz = (float) Math.cos(player.getData(DataAttachments.PLAYER_ROTATION));

        sprintBonus = data.isSprinting() ? 0.75f : 0f;
        float xzMomentumCap = 0.75f + sprintBonus;

        airbrakingOld = data.isAirbraking();
        if (KeyMappings.AIRBRAKE.isDown()) {
            data.setAirbraking(true);

            xzMomentumCap = 0.25f;
            if (xzMomentum >= xzMomentumCap) xzMomentum = Math.max(xzMomentumCap, xzMomentum - 0.0075f - xzMomentum * 0.01f);

        } else {
            data.setAirbraking(false);
        }

        if (airbrakingOld != data.isAirbraking()) PacketDistributor.sendToServer(new SyncAirbraking(data.isAirbraking(), player.getId()));

        if (xzMomentum < xzMomentumCap) xzMomentum = xzMomentum + 0.0075f;

        if (KeyMappings.ASCEND.isDown()) {
            yMomentum = KeyMappings.AIRBRAKE.isDown() ? yMomentum + 0.004f : yMomentum + 0.04f ;
            if (yMomentum >= 0) xzMomentum = Math.max(0.35f, xzMomentum * 0.98f);
        }
        if (KeyMappings.DESCEND.isDown()) {
            yMomentum = KeyMappings.AIRBRAKE.isDown() ? yMomentum - 0.004f : yMomentum - 0.043f;
        }

        yMomentum = Mth.clamp(yMomentum, -1.5f, 0.55f);
        yMomentum = (yMomentum * 0.975f) - 0.001f;
        if (!KeyMappings.DESCEND.isDown() && yMomentum < -0.05f) xzMomentum = Math.min(1.5f, xzMomentum + Math.abs(yMomentum) * 0.0175f);


        player.setDeltaMovement(dx * xzMomentum, yMomentum, dz * xzMomentum);
    }

    private static void handleTakeoff(Player player) {
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        if (!Util.getDino(player).equals(Dinosaurs.PTERANODON)) return;


        if (KeyMappings.TAKEOFF_MAPPING.isDown() && player.onGround()) {
            takeoffHoldTicks++;

            if (takeoffHoldTicks > 5) {

                data.setFlying(true);
                PacketDistributor.sendToServer(new SyncFlightState(true, player.getId()));
                dx = (float) -Math.sin(player.getData(DataAttachments.PLAYER_ROTATION));
                dz = (float) Math.cos(player.getData(DataAttachments.PLAYER_ROTATION));

                yMomentum = 0.5f;
            }
        } else {
            if (takeoffHoldTicks <= 5 && takeoffHoldTicks > 0) {
                float baseJumpStrength = Util.getDino(player).getJumpStrength();

                float dinoJumpStrength = (float) (baseJumpStrength * Mth.lerp(Math.pow(data.getGrowth(), 0.625), 0.2f, 1f));

                if (dinoJumpStrength > 0.25f && player.getData(DataAttachments.KNOCKDOWN_TIME) < 0 && !data.isLayingEggs()) {
                    player.setDeltaMovement(
                            player.getDeltaMovement().x,
                            player.getDeltaMovement().y + dinoJumpStrength,
                            player.getDeltaMovement().z
                    );
                }
            }

            takeoffHoldTicks = 0;
        }

    }

    private static void handleResting(Player player) {
        if (KeyMappings.REST_MAPPING.isDown() ) {

            if (KeyMappings.PAIR_MAPPING.isDown()) return;

            if (!restingPressed && restingTimerOut <= 0) {
                restingPressed = true;
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
        }

        if (restingTimerIn == 1) {
            player.setData(DataAttachments.RESTING_STATE, 2);
            PacketDistributor.sendToServer(new SyncRestingState(2, player.getId()));

        }
    }

    private static int cantUseNUntil = 0;

    private static void handlePairing(Player player) {

        pairingTimeOut--;
        if (player.getData(DataAttachments.PAIRING_STATE) == 1) pairingTimeOut = 55;
        if (pairingTimeOut > 0) {
            turningLocked = true;
            pairMovementLocked = true;
        } else if (pairingTimeOut == 0 && pairMovementLocked) {
            turningLocked = false;
            pairMovementLocked = false;
        }

        if (ClientNestData.ownNest != null && player.getData(DataAttachments.PAIRING_DATA).isPaired() && player.tickCount > cantUseNUntil) {
            if (KeyMappings.PLACE_NEST_MAPPING.consumeClick()) {
                PacketDistributor.sendToServer(new RequestLayEggs());
                cantUseNUntil = player.tickCount + 30;
            }

            return;
        }

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

        if (player.getData(DataAttachments.DINO_DATA).getGrowth() <= 0.999f) return;


        if (KeyMappings.PAIR_MAPPING.isDown()) {
            isPairing = true;
            turningLocked = true;

            player.setData(DataAttachments.ATTEMPTING_PAIRING, true);
            PacketDistributor.sendToServer(new SyncAttemptingPairing(true, player.getId()));

        } else if (isPairing) {
            isPairing = false;
            turningLocked = false;
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


    private static boolean wasEatKeyDown = false;
    private static void handleEating(Player player) {
        if (!wasEatKeyDown && KeyMappings.EAT_MAPPING.isDown() && player.getData(DataAttachments.EATING_TIME) <= 1) {
            PacketDistributor.sendToServer(new RequestFoodSwallow(player.getId()));
        }

        wasEatKeyDown = KeyMappings.EAT_MAPPING.isDown();
    }

    private static void handleDrinking(Player player) {

        if (ClientHitboxData.getOwnHitboxes().isEmpty() || player.getData(DataAttachments.EATING_TIME) > 0) return;

        AssociatedAABB head = ClientHitboxData.getOwnHitboxes().getFirst();
        Position headPos = ClientHitboxData.getPos(head, true);


        if (KeyMappings.DRINK_MAPPING.isDown()) {
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
