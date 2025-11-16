package by.sakeplays.cycle_of_life.event.client.attack;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.event.client.AttackTrigger;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainOne;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainTwo;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.pteranodon.RequestPteranodonFlightPeck;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class PteranodonFlightPeck extends AttackType {
    public PteranodonFlightPeck(Player player) {
        super(player);
    }

    @Override
    public AABB getAttackHitbox() {

        AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).getFirst();
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        float scale = Util.getDino(player).getGrowthCurve().calculate(dinoData.getGrowth(), GrowthCurveStat.SCALE);

        return head.inflate(0.4 * scale, 0.5 * dinoData.getGrowth(), 0.4 * scale)
                .move(0, -1 * scale, 0);
    }

    @Override
    public CustomPacketPayload attackAnimStart() {
        return new SyncAttackMainOne(true, player.getId());
    }

    @Override
    public CustomPacketPayload attackAnimStop() {
        return new SyncAttackMainOne(false, player.getId());
    }

    @Override
    public int altAttackLength() {
        return 0;
    }

    @Override
    public boolean shouldAlign() {
        return false;
    }

    @Override
    public int ticksToAlign() {
        return 0;
    }

    @Override
    public int attackCooldown() {
        return 25;
    }

    @Override
    public int attackPerformTick() {
        return 7;
    }

    @Override
    public Dinosaurs dinosaurType() {
        return Dinosaurs.PTERANODON;
    }

    @Override
    public AttackTrigger attackTrigger() {
        return AttackTrigger.MAIN_1;
    }

    @Override
    public CustomPacketPayload attackPacket() {
        return new RequestPteranodonFlightPeck(getLastTarget(), getLastHitHitboxType().toString());
    }

    @Override
    public int minAttackPriority() {
        return 0;
    }

    @Override
    public boolean additionalCondition() {
        return player.getData(DataAttachments.DINO_DATA).isFlying();
    }
}


