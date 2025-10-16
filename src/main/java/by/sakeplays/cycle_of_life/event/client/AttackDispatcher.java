package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusBite;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusDoubleSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.pachycephalosaurus.RequestPachyBash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.pachycephalosaurus.RequestPachyUpperBash;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;


// Called every client tick.
public class AttackDispatcher {

    private static int attackTimeout = 0;
    private static int attackTimer = 0;
    private static int attackPerformTick = 0;
    private static float desiredAttackAngle = 0;
    public static boolean isAltAttacking = false;

    private static Pair<Player, List<HitboxType>> attackResult(Player attacker, AABB attackHitbox, int minPriority) {
        Player target = null;
        List<HitboxType> hitboxes = new ArrayList<>();

        for (AssociatedAABB hb : ClientHitboxData.HITBOXES) {
            if (hb.getPlayer() != attacker && hb.intersects(attackHitbox)) {
                hitboxes.add(hb.getType());
                target = hb.getPlayer();
            }
        }

        hitboxes.removeIf(hb -> hb.getPriority() < minPriority);

        return Pair.of(target, hitboxes);
    }

    private static int iterationAlt = 0;
    private static int iterationMain = 0;
    private static void handleAttack(AttackType attackType, AABB attackHitbox) {

        Player player = Minecraft.getInstance().player;

        float yRot = player.getYRot();
        float additionalTurn = player.getData(DataAttachments.ADDITIONAL_TURN) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);
        float targetYaw = yRot + additionalTurn;

        if (attackType.isAltAttack() || attackTimer > 0) {
            if (isMainAltAttacking() && attackType.getAttackTrigger() == AttackTrigger.ALT_1) {
                desiredAttackAngle = Mth.wrapDegrees(targetYaw - turnDegree * Mth.RAD_TO_DEG - additionalTurn) * Mth.DEG_TO_RAD;
                attackTimeout = attackType.getCooldown();
                attackTimer = attackType.getAltAttackLength();
                attackPerformTick = attackType.getAltAttackLength() - attackType.getTicksToAlign();
                HandleKeys.turningLocked = true;

                player.setData(DataAttachments.ALT_ATTACK, true);
                PacketDistributor.sendToServer(new SyncAttackTurnaround(true, player.getId()));  // animation
            } else if (isSecondaryAltAttacking()  && attackType.getAttackTrigger() == AttackTrigger.ALT_2) {
                // TODO: no dinos use this yet
            }

            if (iterationAlt > 0) return;
            iterationAlt++;

            if (attackTimer > attackPerformTick) {
                float newTurnDegree = player.getData(DataAttachments.PLAYER_ROTATION) +
                        (desiredAttackAngle / Math.max(1, attackType.getTicksToAlign()));

                player.setData(DataAttachments.PLAYER_ROTATION, newTurnDegree);
                PacketDistributor.sendToServer(new SyncPlayerRotation(newTurnDegree, player.getId()));
            } else if (attackTimer == attackPerformTick) {

                Pair<Player, List<HitboxType>> target = attackResult(player, attackHitbox, attackType.getMinPriority());
                int targetID = target.first() == null ? -1 : target.first().getId();

                sendAttackPacket(attackType, targetID, HitboxType.withHighestPriority(target.second()).toString());

            }
        } else {

            if (isMainAttacking() && attackType.getAttackTrigger() == AttackTrigger.MAIN_1) {

                if (Util.getDino(player).equals(Dinosaurs.PACHYCEPHALOSAURUS) && !player.getData(DataAttachments.DINO_DATA).isCharging()) return;
                attackTimeout = attackType.getCooldown();

                player.setData(DataAttachments.ATTACK_MAIN_1, true);
                PacketDistributor.sendToServer(new SyncAttackMainOne(true, player.getId()));

                Pair<Player, List<HitboxType>> target = attackResult(player, attackHitbox, attackType.getMinPriority());
                int targetID = target.first() == null ? -1 : target.first().getId();

                sendAttackPacket(attackType, targetID, HitboxType.withHighestPriority(target.second()).toString());

            } else if (isSecondaryAttacking() && attackType.getAttackTrigger() == AttackTrigger.MAIN_2) {

                attackTimeout = attackType.getCooldown();

                player.setData(DataAttachments.ATTACK_MAIN_2, true);
                PacketDistributor.sendToServer(new SyncAttackMainTwo(true, player.getId()));

                Pair<Player, List<HitboxType>> target = attackResult(player, attackHitbox, attackType.getMinPriority());
                int targetID = target.first() == null ? -1 : target.first().getId();

                sendAttackPacket(attackType, targetID, HitboxType.withHighestPriority(target.second()).toString());

            }
        }
    }


    private static boolean chargingOld = false;
    private static void handleHoldStates() {
        Player player = Minecraft.getInstance().player;

        switch (Util.getDino(player)) {
            case PACHYCEPHALOSAURUS -> {
                if (KeyMappings.MAIN_ATTACK_MAPPING_2.isDown() && player.getData(DataAttachments.DINO_DATA).isSprinting() && attackTimeout <= 0) {
                    player.getData(DataAttachments.DINO_DATA).setCharging(true);
                    HandleKeys.additionalSpeed = 1.2f;
                } else {
                    player.getData(DataAttachments.DINO_DATA).setCharging(false);
                    HandleKeys.additionalSpeed = 1f;
                }

                if (chargingOld != player.getData(DataAttachments.DINO_DATA).isCharging()) {
                    PacketDistributor.sendToServer(new SyncCharging(player.getData(DataAttachments.DINO_DATA).isCharging(), player.getId()));
                }
                chargingOld = player.getData(DataAttachments.DINO_DATA).isCharging();
            }
        }
    }

    private static void sendAttackPacket(AttackType type, int targetId, String hitboxType) {
        switch (type) {
            case DEINONYCHUS_BITE -> PacketDistributor.sendToServer(new RequestDeinonychusBite(targetId, hitboxType));
            case DEINONYCHUS_SLASH -> PacketDistributor.sendToServer(new RequestDeinonychusSlash(targetId, hitboxType));
            case DEINONYCHUS_SLASH_ALT -> PacketDistributor.sendToServer(new RequestDeinonychusDoubleSlash(targetId, hitboxType));
            case PACHYCEPHALOSAURUS_BASH -> PacketDistributor.sendToServer(new RequestPachyUpperBash(targetId, hitboxType));
            case PACHYCEPHALOSAURUS_CHARGED_BASH -> PacketDistributor.sendToServer(new RequestPachyBash(targetId, hitboxType));
        }
    }

    private static boolean isMainAltAttacking() {
        Player player = Minecraft.getInstance().player;

        return KeyMappings.MAIN_ATTACK_MAPPING.isDown() &&
                KeyMappings.DIRECTIONAL_ATTACK.isDown() &&
                player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && attackTimeout <= 0;
    }

    private static boolean isSecondaryAltAttacking() {
        Player player = Minecraft.getInstance().player;

        return KeyMappings.MAIN_ATTACK_MAPPING_2.isDown() &&
                KeyMappings.DIRECTIONAL_ATTACK.isDown() &&
                player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && attackTimeout <= 0;
    }

    private static boolean isMainAttacking() {
        Player player = Minecraft.getInstance().player;

        return KeyMappings.MAIN_ATTACK_MAPPING.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && attackTimeout <= 0;
    }

    private static boolean isSecondaryAttacking() {
        Player player = Minecraft.getInstance().player;

        return KeyMappings.MAIN_ATTACK_MAPPING_2.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && attackTimeout <= 0;
    }

    public static void tick() {
        Player player = Minecraft.getInstance().player;

        if (player == null) return;
        if (ClientHitboxData.hitboxMap.get(player.getId()) == null) return;

        attackTimeout--;
        attackTimer--;

        isAltAttacking = attackTimer > 0;

        if (player.getData(DataAttachments.ATTACK_MAIN_1)) {
            player.setData(DataAttachments.ATTACK_MAIN_1, false);
            PacketDistributor.sendToServer(new SyncAttackMainOne(false, player.getId()));
        }
        if (player.getData(DataAttachments.ATTACK_MAIN_2)) {
            player.setData(DataAttachments.ATTACK_MAIN_2, false);
            PacketDistributor.sendToServer(new SyncAttackMainTwo(false, player.getId()));
        }
        if (player.getData(DataAttachments.ALT_ATTACK)) {
            player.setData(DataAttachments.ALT_ATTACK, false);
            PacketDistributor.sendToServer(new SyncAttackTurnaround(false, player.getId()));
        }

        handleHoldStates();
        AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).getFirst();
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        switch (Util.getDino(player)) {

            case DEINONYCHUS -> {

                AABB biteHitbox = head.inflate(0.2 * dinoData.getGrowth(), 0.3 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth());
                AABB slashHitbox = head.inflate(0.2 * dinoData.getGrowth(), 0.5 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                        .move(0, -0.8 * dinoData.getGrowth(), 0);

                handleAttack(AttackType.DEINONYCHUS_SLASH_ALT, slashHitbox);
                handleAttack(AttackType.DEINONYCHUS_BITE, biteHitbox);
                handleAttack(AttackType.DEINONYCHUS_SLASH, slashHitbox);
            }

            case PACHYCEPHALOSAURUS -> {

                AABB bash = head.inflate(0.2 * dinoData.getGrowth(), 0.8 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                        .move(0, -0.6 * dinoData.getGrowth(), 0);

                handleAttack(AttackType.PACHYCEPHALOSAURUS_BASH, bash);
                handleAttack(AttackType.PACHYCEPHALOSAURUS_CHARGED_BASH, bash);

            }
        }

        iterationAlt = 0;

    }
}
