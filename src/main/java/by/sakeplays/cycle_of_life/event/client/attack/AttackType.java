package by.sakeplays.cycle_of_life.event.client.attack;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.event.client.AttackTrigger;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncPlayerRotation;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AttackType {

    protected int currentTick = 0;
    protected Player player;
    public boolean isActive = false;
    protected float desiredAttackAngle = 0;
    protected boolean shouldAnimStop = false;
    protected int lastTargetId = -1;
    protected HitboxType lastHitHitboxType = HitboxType.NONE;

    private Pair<Player, List<HitboxType>> attackResult(Player attacker, AABB attackHitbox, int minPriority) {
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

    public AttackType(Player player) {
        this.player = player;
    }

    protected void updatePlayer(Player player) {
        this.player = player;
    }

    protected void tick() {
        if (Util.getDino(player) != dinosaurType()) {
            currentTick = 0;
            isActive = false;
            AttackDispatcher.isAltAttacking = false;
            shouldAnimStop = true;
            return;
        }

        if (shouldAnimStop) {
            PacketDistributor.sendToServer(attackAnimStop());
            shouldAnimStop = false;
        }

        if (!isActive && AttackDispatcher.usedAttackTrigger() == attackTrigger() && AttackDispatcher.attackTimeout <= 0 && additionalCondition()) {
            isActive = true;

            AttackDispatcher.attackTimeout = attackCooldown();

            if (shouldAlign()) {
                AttackDispatcher.isAltAttacking = true;
                float targetYaw = player.getYRot();
                float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);

                desiredAttackAngle = Mth.wrapDegrees(targetYaw - turnDegree * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
            }

            PacketDistributor.sendToServer(attackAnimStart());
            shouldAnimStop = true;
        }

        if (isActive) {
            currentTick++;
            if (currentTick >= attackCooldown()) {
                isActive = false;
                currentTick = 0;
                AttackDispatcher.isAltAttacking = false;
                return;
            }

            if (shouldAlign() && currentTick <= ticksToAlign()) align();

            if (shouldAlign() && currentTick > altAttackLength()) AttackDispatcher.isAltAttacking = false;

            specialAction();

            if (currentTick == attackPerformTick()) {
                if (attackPacket() != null) {

                    Pair<Player, List<HitboxType>> target = attackResult(player, getAttackHitbox(), minAttackPriority());
                    lastTargetId = target.first() == null ? -1 : target.first().getId();
                    lastHitHitboxType = HitboxType.withHighestPriority(target.second());
                    PacketDistributor.sendToServer(attackPacket());
                }
            }
        }
    }

    protected int getLastTarget() {
        return lastTargetId;
    }

    public abstract AABB getAttackHitbox();

    protected HitboxType getLastHitHitboxType() {
        return lastHitHitboxType;
    }

    private void align() {

        float newTurnDegree = player.getData(DataAttachments.PLAYER_ROTATION) +
                (desiredAttackAngle / Math.max(1, ticksToAlign()));

        player.setData(DataAttachments.PLAYER_ROTATION, newTurnDegree);
        PacketDistributor.sendToServer(new SyncPlayerRotation(newTurnDegree, player.getId()));
    }

    public abstract CustomPacketPayload attackAnimStart();

    public abstract CustomPacketPayload attackAnimStop();

    public abstract int altAttackLength();

    public abstract boolean shouldAlign();

    public abstract int ticksToAlign();

    public abstract int attackCooldown();

    public abstract int attackPerformTick();

    public abstract Dinosaurs dinosaurType();

    public abstract AttackTrigger attackTrigger();

    public abstract @Nullable CustomPacketPayload attackPacket();

    public abstract int minAttackPriority();

    public boolean additionalCondition() {
        return true;
    }

    public void specialAction() {}

    public boolean shouldForceWalk() {
        return false;
    }
}
