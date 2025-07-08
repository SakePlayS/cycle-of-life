package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HandleKeys {

    public static float turnMultiplier = 0f;
    private static float speed = 0;
    private static float maxSpeed = 0;
    private static float trueMaxSpeed = 0;
    private static int tick = 0;
    private static int attackCooldown = 0;
    private static int backwardsAttackTimer = 0;
    private static boolean turningLocked = false;
    private static boolean canMove = true;
    private static boolean restingPressed = false;
    private static int restingTimerOut = 0;
    private static int restingTimerIn = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }


        attackCooldown--;
        backwardsAttackTimer--;

        handleSprint(player);
        handleTurning(player);
        handleForwardMovement(player);

        handleMainAttack(player);
        handleResting(player);

        boolean sliding = (!player.getData(DataAttachments.DINO_DATA).isMoving() &&
                (player.getDeltaMovement().x() != 0 || player.getDeltaMovement().z() != 0));

        player.getData(DataAttachments.DINO_DATA).setSliding(sliding);
        PacketDistributor.sendToServer(new SyncIsSliding(sliding, player.getId()));

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

        if (turningLocked) return;
        if (!canMove) return;

        float turnSpeed = Util.getTurnSpeed(player) * Mth.DEG_TO_RAD;;

        if (KeyMappingsEvent.LEFT_MAPPING.isDown()) {
            turnMultiplier = turnMultiplier + turnSpeed / 4;
            turnMultiplier = Math.min(1, turnMultiplier + turnSpeed);

            float desiredTurnDegree = player.getData(DataAttachments.PLAYER_TURN)
                    + turnSpeed * Util.getTurnPenalty(player);


            player.setData(DataAttachments.PLAYER_TURN, desiredTurnDegree);
            PacketDistributor.sendToServer(new SyncTurnDegree(desiredTurnDegree, player.getId()));
        }

        if (KeyMappingsEvent.RIGHT_MAPPING.isDown()) {
            turnMultiplier = turnMultiplier - turnSpeed / 4;
            turnMultiplier = Math.max(-1, turnMultiplier - turnSpeed);


            float desiredTurnDegree = player.getData(DataAttachments.PLAYER_TURN) - turnSpeed * Util.getTurnPenalty(player);

            player.setData(DataAttachments.PLAYER_TURN,desiredTurnDegree);
            PacketDistributor.sendToServer(new SyncTurnDegree(desiredTurnDegree, player.getId()));
        }

        if (!KeyMappingsEvent.RIGHT_MAPPING.isDown() && !KeyMappingsEvent.LEFT_MAPPING.isDown()) {
            if (turnMultiplier > 0) {
                turnMultiplier = Math.max(0, turnMultiplier - turnSpeed / 4);
            } else {
                turnMultiplier = Math.min(0, turnMultiplier + turnSpeed / 4);
            }
        }

        player.setData(DataAttachments.TURN_PROGRESS, turnMultiplier);
        PacketDistributor.sendToServer(new SyncTurnProgress(turnMultiplier, player.getId()));
    }

    private static void handleForwardMovement(Player player) {
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        float turnDegree = player.getData(DataAttachments.PLAYER_TURN);
        float walkSpeed = Util.getWalkSpeed(player);
        float sprintSpeed = Util.getSprintSpeed(player);
        float acceleration = Util.getAcceleration(player);
        float swimSpeed = Util.getSwimSpeed(player);
        float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();

        if (player.onGround() || player.isInWater()) {

            if (player.isInWater()) walkSpeed = walkSpeed/3;

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

        if (!canMove) return;

        float dx = (float) Math.sin(turnDegree);
        float dz = (float) Math.cos(turnDegree);

        player.setDeltaMovement(dx * speed, player.getDeltaMovement().y, dz * speed);
    }

    private static void handleMainAttack(Player player) {
        if (!canMove) return;


        float turnDegree = player.getData(DataAttachments.PLAYER_TURN);

        float dirX = (float) Math.sin(turnDegree);
        float dirZ = (float) Math.cos(turnDegree);

        if (player.getData(DataAttachments.ATTACK_MAIN_1)) {
            player.setData(DataAttachments.ATTACK_MAIN_1, false);
            PacketDistributor.sendToServer(new SyncAttackMainOne(false, player.getId()));
        }

        if (player.getData(DataAttachments.ATTACK_TURNAROUND)) {
            player.setData(DataAttachments.ATTACK_TURNAROUND, false);
            PacketDistributor.sendToServer(new SyncAttackTurnaround(false, player.getId()));
        }


        double x = player.getX() + dirX * 1.3;
        double y = player.getY();
        double z = player.getZ() + dirZ * 1.3;
        double size = 0.5;

        AABB hitbox = new AABB(
                x - size, y, z - size,
                x + size, y + 1.2d, z + size
        );


        if (KeyMappingsEvent.MAIN_ATTACK_MAPPING.isDown() && !KeyMappingsEvent.DIRECTIONAL_ATTACK.isDown() && attackCooldown <= 0) {
            attackCooldown = 20;

            player.setData(DataAttachments.ATTACK_MAIN_1, true);
            PacketDistributor.sendToServer(new SyncAttackMainOne(true, player.getId()));

            List<HitboxEntity> hitboxes = player.level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), hitbox, e -> true);

            int iterations = 0;
            for (HitboxEntity target : hitboxes) {

                if (target.getPlayer() != player && iterations <= 0) {
                    Player targetPlayer = target.getPlayer();

                    iterations++;
                    player.level().playLocalSound(targetPlayer.getOnPos(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 2, 1, true);
                    Util.dealDamage(targetPlayer, 7f * target.getDamageFactor(), 0.06f  * target.getDamageFactor());
                    Util.addStamina(player, -10f);
                }
            }

        } else if (KeyMappingsEvent.MAIN_ATTACK_MAPPING.isDown() && KeyMappingsEvent.DIRECTIONAL_ATTACK.isDown() && attackCooldown <= 0) {
            attackCooldown = 25;
            backwardsAttackTimer = 15;
            turningLocked = true;

            player.setData(DataAttachments.ATTACK_TURNAROUND, true);
            PacketDistributor.sendToServer(new SyncAttackTurnaround(true, player.getId()));

        }

        if (backwardsAttackTimer > 8) {
            player.setData(DataAttachments.PLAYER_TURN, player.getData(DataAttachments.PLAYER_TURN) + 0.4487974545f);
            PacketDistributor.sendToServer(new SyncTurnDegree(player.getData(DataAttachments.PLAYER_TURN) + 0.4487974545f, player.getId()));
        }

        if (backwardsAttackTimer == 8) {
            List<HitboxEntity> hitboxes = player.level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), hitbox, e -> true);

            int iterations = 0;
            for (HitboxEntity target : hitboxes) {

                if (target.getPlayer() != player && iterations <= 0) {
                    Player targetPlayer = target.getPlayer();

                    iterations++;
                    player.level().playLocalSound(targetPlayer.getOnPos(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 2, 1, true);
                    Util.dealDamage(targetPlayer, 15f  * target.getDamageFactor(), 0.15f  * target.getDamageFactor());
                    Util.addStamina(player, -30f);
                }
            }
        }

        if (backwardsAttackTimer <= 0) {
            turningLocked = false;
        }
    }

    private static void handleResting(Player player) {
        if (KeyMappingsEvent.REST_MAPPING.isDown() ) {
            if (!restingPressed && restingTimerOut <= 0) {
                restingPressed = true;
                canMove = false;
                if (player.getData(DataAttachments.RESTING_STATE) == 0 && restingTimerIn == 0) {
                    restingTimerIn = 21;

                    player.setData(DataAttachments.RESTING_STATE, 1);
                    PacketDistributor.sendToServer(new SyncRestingState(1, player.getId()));
                    player.sendSystemMessage(Component.literal("1"));

                }

                if (player.getData(DataAttachments.RESTING_STATE) == 2) {
                    player.sendSystemMessage(Component.literal("3"));
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
            player.sendSystemMessage(Component.literal("0"));

        }

        if (restingTimerIn == 1) {
            player.setData(DataAttachments.RESTING_STATE, 2);
            PacketDistributor.sendToServer(new SyncRestingState(2, player.getId()));
            player.sendSystemMessage(Component.literal("2"));

        }
    }


}
