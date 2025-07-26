package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusBite;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusDoubleSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.pachycephalosaurus.RequestPachyBash;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;


// Called every client tick.
public class Attacks {

    private static int attackTimeout = 0;
    private static int attackTimer = 0;
    private static float desiredAttackAngle = 0;
    private static boolean wasTurningUnlocked = false;


    private static Pair<Player, String> getTarget(Player player, AABB attackHitbox) {
        for (AssociatedAABB hb : ClientHitboxData.HITBOXES) {
            if (hb.getPlayer() != player) {
                if (hb.intersects(attackHitbox)) {
                    return Pair.of(hb.getPlayer(), hb.getType().toString());
                }
            }
        }

        return null;
    }

    private static Pair<Player, String> getTargetAtLeastBody(Player player, AABB attackHitbox) {
        for (AssociatedAABB hb : ClientHitboxData.HITBOXES) {
            if (hb.getPlayer() != player) {
                if (hb.intersects(attackHitbox) && (hb.getType() == HitboxType.BODY1 || hb.getType() == HitboxType.BODY2 || hb.getType() == HitboxType.HEAD)) {
                    return Pair.of(hb.getPlayer(), hb.getType().toString());
                }
            }
        }

        return null;
    }

    public static void deinonychus(Player player) {
        float yRot = player.getYRot();
        float additionalTurn = player.getData(DataAttachments.ADDITIONAL_TURN) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);
        float targetYaw = yRot + additionalTurn;

        attackTimeout--;
        attackTimer--;

        if (player.getData(DataAttachments.ATTACK_MAIN_1)) {
            player.setData(DataAttachments.ATTACK_MAIN_1, false);
            PacketDistributor.sendToServer(new SyncAttackMainOne(false, player.getId()));
        }
        if (player.getData(DataAttachments.ATTACK_MAIN_2)) {
            player.setData(DataAttachments.ATTACK_MAIN_2, false);
            PacketDistributor.sendToServer(new SyncAttackMainTwo(false, player.getId()));
        }
        if (player.getData(DataAttachments.ATTACK_TURNAROUND)) {
            player.setData(DataAttachments.ATTACK_TURNAROUND, false);
            PacketDistributor.sendToServer(new SyncAttackTurnaround(false, player.getId()));
        }


        if (!HandleKeys.canMove) return;
        if (KeyMappings.PAIR_MAPPING.isDown()) return;

        if ((KeyMappings.MAIN_ATTACK_MAPPING.isDown() && KeyMappings.DIRECTIONAL_ATTACK.isDown() // ALT ATTACK (DOUBLE SLASH) (good to prevent "tail riding")
                && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0) || attackTimer > 0)  {

            if (attackTimer == 1 && !wasTurningUnlocked) {
                HandleKeys.turningLocked = false;
                wasTurningUnlocked = true;
            }

            if (attackTimer == 8) {

                DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

                AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).get(0);
                AABB slash = head.inflate(0.2 * dinoData.getGrowth(), 0.5 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                        .move(0, -0.8 * dinoData.getGrowth(), 0);

                Pair<Player, String> target = getTarget(player, slash);

                if (target != null) {
                    PacketDistributor.sendToServer(new RequestDeinonychusDoubleSlash(target.first().getId(), target.second()));
                }
            }

            if (attackTimer > 8) {
                float newTurnDegree = player.getData(DataAttachments.PLAYER_ROTATION) +
                        (desiredAttackAngle / 7f);

                player.setData(DataAttachments.PLAYER_ROTATION, newTurnDegree);
                PacketDistributor.sendToServer(new SyncPlayerRotation(newTurnDegree, player.getId()));
            }

             if (player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && attackTimeout <= 0) {
                 desiredAttackAngle = Mth.wrapDegrees(targetYaw - turnDegree * Mth.RAD_TO_DEG - additionalTurn) * Mth.DEG_TO_RAD;
                 attackTimeout = 16;
                 attackTimer = 16;
                 HandleKeys.turningLocked = true;
                 wasTurningUnlocked = false;

                 player.setData(DataAttachments.ATTACK_TURNAROUND, true);
                 PacketDistributor.sendToServer(new SyncAttackTurnaround(true, player.getId()));  // animation
             }

        } else if (KeyMappings.MAIN_ATTACK_MAPPING.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0
                && attackTimeout <= 0) { // MAIN ATTACK ONE (BITE)
            attackTimeout = 12;


            player.setData(DataAttachments.ATTACK_MAIN_1, true);
            PacketDistributor.sendToServer(new SyncAttackMainOne(true, player.getId()));  // animation

            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).get(0);
            AABB bite = head.inflate(0.2 * dinoData.getGrowth(), 0.3 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth());

            Pair<Player, String> target = getTarget(player, bite);

            if (target != null) {
                PacketDistributor.sendToServer(new RequestDeinonychusBite(target.first().getId(), target.second()));
            }

        } else if (KeyMappings.MAIN_ATTACK_MAPPING_2.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0
                && attackTimeout <= 0) { // MAIN ATTACK TWO (SLASH)
            attackTimeout = 12;

            player.setData(DataAttachments.ATTACK_MAIN_2, true);
            PacketDistributor.sendToServer(new SyncAttackMainTwo(true, player.getId())); // animation

            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).get(0);
            AABB slash = head.inflate(0.2 * dinoData.getGrowth(), 0.5 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                    .move(0, -0.8 * dinoData.getGrowth(), 0);

            Pair<Player, String> target = getTarget(player, slash);

            if (target != null) {
                PacketDistributor.sendToServer(new RequestDeinonychusSlash(target.first().getId(), target.second()));
            }
        }


    }



    public static void pachycephalosaurus(Player player) {
        float yRot = player.getYRot();
        float additionalTurn = player.getData(DataAttachments.ADDITIONAL_TURN) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);
        float targetYaw = yRot + additionalTurn;

        if (player.getData(DataAttachments.ATTACK_MAIN_1)) {
            player.setData(DataAttachments.ATTACK_MAIN_1, false);
            PacketDistributor.sendToServer(new SyncAttackMainOne(false, player.getId()));
        }

        if (!HandleKeys.canMove) return;
        if (KeyMappings.PAIR_MAPPING.isDown()) return;

        if (KeyMappings.MAIN_ATTACK_MAPPING.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && player.getData(DataAttachments.DINO_DATA).isSprinting()) {
            if (HandleKeys.attackTimeout <= 0) {
                HandleKeys.additionalSpeed = 1.15f;

                if (!player.getData(DataAttachments.DINO_DATA).isCharging()) {
                    player.getData(DataAttachments.DINO_DATA).setCharging(true);
                    PacketDistributor.sendToServer(new SyncCharging(true, player.getId()));

                }
            }
        } else {
            if (player.getData(DataAttachments.DINO_DATA).isCharging()) {
                player.getData(DataAttachments.DINO_DATA).setCharging(false);
                PacketDistributor.sendToServer(new SyncCharging(false, player.getId()));

                player.setData(DataAttachments.ATTACK_MAIN_1, true);
                PacketDistributor.sendToServer(new SyncAttackMainOne(true, player.getId()));
                HandleKeys.attackTimeout = 40;

                DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

                AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).get(0);
                AABB bash = head.inflate(0.2 * dinoData.getGrowth(), 0.8 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                        .move(0, -0.4 * dinoData.getGrowth(), 0);

                Pair<Player, String> target = getTargetAtLeastBody(player, bash);

                if (target != null) {
                    PacketDistributor.sendToServer(new RequestPachyBash(target.first().getId(), target.second()));
                }

            }
        }
    }
}
