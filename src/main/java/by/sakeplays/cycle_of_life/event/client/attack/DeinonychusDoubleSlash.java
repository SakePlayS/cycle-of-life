package by.sakeplays.cycle_of_life.event.client.attack;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.event.client.AttackTrigger;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainTwo;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackTurnaround;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusDoubleSlash;
import by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus.RequestDeinonychusSlash;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class DeinonychusDoubleSlash extends AttackType {
    public DeinonychusDoubleSlash(Player player) {
        super(player);
    }

    @Override
    public AABB getAttackHitbox() {

        AssociatedAABB head = ClientHitboxData.hitboxMap.get(player.getId()).getFirst();
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        float scale = Util.getDino(player).getGrowthCurve().calculate(dinoData.getGrowth(), GrowthCurveStat.SCALE);
        float playerRot = player.getData(DataAttachments.PLAYER_ROTATION);
        float x = (float) -Math.sin(playerRot) * 0.5f * scale;
        float z = (float) Math.cos(playerRot) * 0.5f * scale;
        float y = (float) Math.cos(player.getXRot() * Mth.DEG_TO_RAD  + Mth.HALF_PI) * 0.5f * scale;

        return head.inflate(0.35 * scale, 1 * scale, 0.35 * scale)
                .move(0 + x, -0.4 * scale + y, 0 + z);
    }

    @Override
    public CustomPacketPayload attackAnimStart() {
        return new SyncAttackTurnaround(true, player.getId());
    }

    @Override
    public CustomPacketPayload attackAnimStop() {
        return new SyncAttackTurnaround(false, player.getId());
    }

    @Override
    public int altAttackLength() {
        return 12;
    }

    @Override
    public boolean shouldAlign() {
        return true;
    }

    @Override
    public int ticksToAlign() {
        return 10;
    }

    @Override
    public int attackCooldown() {
        return 22;
    }

    @Override
    public int attackPerformTick() {
        return 10;
    }

    @Override
    public Dinosaurs dinosaurType() {
        return Dinosaurs.DEINONYCHUS;
    }

    @Override
    public AttackTrigger attackTrigger() {
        return AttackTrigger.ALT_1;
    }

    @Override
    public CustomPacketPayload attackPacket() {
        return new RequestDeinonychusDoubleSlash(getLastTarget(), getLastHitHitboxType().toString());
    }

    @Override
    public int minAttackPriority() {
        return 0;
    }
}
