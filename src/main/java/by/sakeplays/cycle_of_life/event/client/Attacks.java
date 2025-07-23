package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainOne;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainTwo;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackTurnaround;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnDegree;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusBite;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusDoubleSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusDoubleSlashStart;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusSlash;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class Attacks {

    public static void deinonychus(Player player) {
        float yRot = player.getYRot();
        float additionalTurn = player.getData(DataAttachments.ADDITIONAL_TURN) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_TURN);
        float targetYaw = yRot + additionalTurn;


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

        if ((KeyMappings.MAIN_ATTACK_MAPPING.isDown() && KeyMappings.DIRECTIONAL_ATTACK.isDown() // ALT ATTACK
                && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0) || HandleKeys.attackTimer > 0)  {

             if (player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0 && HandleKeys.attackTimeout <= 0) {
                PacketDistributor.sendToServer(new RequestDeinonychusDoubleSlashStart(player.getId()));
                HandleKeys.attackTimeout = 16;

             }

        } else if (KeyMappings.MAIN_ATTACK_MAPPING.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0
                && HandleKeys.attackTimeout <= 0) { // MAIN ATTACK ONE
            HandleKeys.attackTimeout = 12;
            PacketDistributor.sendToServer(new RequestDeinonychusBite());
        } else if (KeyMappings.MAIN_ATTACK_MAPPING_2.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0
                && HandleKeys.attackTimeout <= 0) { // MAIN ATTACK TWO
            HandleKeys.attackTimeout = 12;
            PacketDistributor.sendToServer(new RequestDeinonychusSlash());
        }

        if (HandleKeys.attackTimer == 0) {
            HandleKeys.turningLocked = false;
        }
    }

    public static void pachycephalosaurus(Player player) {
        float yRot = player.getYRot();
        float additionalTurn = player.getData(DataAttachments.ADDITIONAL_TURN) * Mth.DEG_TO_RAD;
        float turnDegree = player.getData(DataAttachments.PLAYER_TURN);
        float targetYaw = yRot + additionalTurn;

        if (player.getData(DataAttachments.ATTACK_MAIN_1)) {
            player.setData(DataAttachments.ATTACK_MAIN_1, false);
            PacketDistributor.sendToServer(new SyncAttackMainOne(false, player.getId()));
        }

        if (!HandleKeys.canMove) return;
        if (KeyMappings.PAIR_MAPPING.isDown()) return;

        if (KeyMappings.MAIN_ATTACK_MAPPING.isDown() && player.getData(DataAttachments.ATTACK_COOLDOWN) <= 0) {
            if (HandleKeys.attackTimeout <= 0) {
                HandleKeys.additionalSpeed = 1.15f;

                if (!player.getData(DataAttachments.DINO_DATA).isCharging()) {
                    player.getData(DataAttachments.DINO_DATA).setCharging(true);
                }
            }
        } else {
            if (player.getData(DataAttachments.DINO_DATA).isCharging()) {
                player.getData(DataAttachments.DINO_DATA).setCharging(false);
                player.setData(DataAttachments.ATTACK_MAIN_1, true);
                PacketDistributor.sendToServer(new SyncAttackMainOne(true, player.getId()));
                HandleKeys.attackTimeout = 12;
            }
        }
    }
}
