package by.sakeplays.cycle_of_life.event.client.attack;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.event.client.AttackTrigger;
import by.sakeplays.cycle_of_life.event.client.HandleKeys;
import by.sakeplays.cycle_of_life.event.client.KeyMappings;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;


// Called every client tick.
public class AttackDispatcher {

    public static boolean isAltAttacking = false;
    public static int attackTimeout = 0;
    private static boolean chargingOld = false;

    public static List<AttackType> attackTypes = new ArrayList<>();

    private static void handleHoldStates() {
        Player player = Minecraft.getInstance().player;

        switch (Util.getDino(player)) {
            case PACHYCEPHALOSAURUS -> {
                if (usedAttackTrigger() == AttackTrigger.MAIN_2 && player.getData(DataAttachments.DINO_DATA).isSprinting() && attackTimeout <= 0) {
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

    public static AttackTrigger usedAttackTrigger() {
        Player player = Minecraft.getInstance().player;

        if (player == null) return AttackTrigger.NONE;

        if (KeyMappings.MAIN_ATTACK_MAPPING.isDown() && KeyMappings.DIRECTIONAL_ATTACK.isDown()) return AttackTrigger.ALT_1;
        if (KeyMappings.MAIN_ATTACK_MAPPING_2.isDown() && KeyMappings.DIRECTIONAL_ATTACK.isDown()) return AttackTrigger.ALT_2;

        if (KeyMappings.MAIN_ATTACK_MAPPING.isDown()) return AttackTrigger.MAIN_1;
        if (KeyMappings.MAIN_ATTACK_MAPPING_2.isDown()) return AttackTrigger.MAIN_2;

        return AttackTrigger.NONE;
    }


    public static void tick() {
        Player player = Minecraft.getInstance().player;

        if (player == null) return;
        if (ClientHitboxData.hitboxMap.get(player.getId()) == null) return;

        attackTimeout--;

        init(player);

        for (AttackType type : attackTypes) {
            type.updatePlayer(player);
            type.tick();
            if (type.isActive) break;
        }

        handleHoldStates();

    }


    private static void init(Player player) {
        if (attackTypes.isEmpty()) {
            attackTypes.add(new DeinonychusBite(player));
            attackTypes.add(new DeinonychusDoubleSlash(player));
            attackTypes.add(new DeinonychusSingleSlash(player));
            attackTypes.add(new PachycephalosaurusChargedBash(player));
            attackTypes.add(new PachycephalosaurusBash(player));
            attackTypes.add(new PteranodonFlightPeck(player));

        }
    }
}
