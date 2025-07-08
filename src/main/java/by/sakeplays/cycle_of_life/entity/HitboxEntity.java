package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.HitboxData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Objects;

public class HitboxEntity extends Entity {

    public static final EntityDataAccessor<Integer> PLAYER_ID =
            SynchedEntityData.defineId(HitboxEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TAIL_SEGMENT =
            SynchedEntityData.defineId(HitboxEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DAMAGE_FACTOR =
            SynchedEntityData.defineId(HitboxEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> WIDTH =
            SynchedEntityData.defineId(HitboxEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> HEIGHT =
            SynchedEntityData.defineId(HitboxEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<String> TYPE =
            SynchedEntityData.defineId(HitboxEntity.class, EntityDataSerializers.STRING);

    public HitboxEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PLAYER_ID, 0);
        builder.define(DAMAGE_FACTOR, 1f);
        builder.define(WIDTH, 0.75f);
        builder.define(HEIGHT, 0.75f);
        builder.define(TYPE, "HEAD");
        builder.define(TAIL_SEGMENT, 0);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount < 2) return;

        if (getPlayer() == null) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        if (getPlayer().isRemoved()) this.remove(RemovalReason.DISCARDED);

        HitboxData data = getPlayer().getData(DataAttachments.HITBOX_DATA);
        float growth = getPlayer().getData(DataAttachments.DINO_DATA).getGrowth();

        switch (getHitboxType()) {

            case "HEAD" -> {
                this.setDamageFactor(1.75f);
                setHitboxSize(0.5f * growth, 0.35f * growth);
                if (!this.level().isClientSide()) this.setPos(data.getHeadHitboxPos().toVec3());
            }
            case "BODY1" -> {
                this.setDamageFactor(1f);
                setHitboxSize(0.6f * growth, 1.3f * growth);
                if (!this.level().isClientSide()) this.setPos(data.getBody1Pos().toVec3());
            }
            case "BODY2" -> {
                this.setDamageFactor(0.8f);
                setHitboxSize(0.6f * growth, 1.3f * growth);
                if (!this.level().isClientSide()) this.setPos(data.getBody2Pos().toVec3());
            }
            case "TAIL1" -> {
                this.setDamageFactor(0.5f);
                setHitboxSize(0.5f * growth, 0.5f * growth);
                if (!this.level().isClientSide()) this.setPos(data.getTail1Pos().toVec3());
            }
            case "TAIL2" -> {
                this.setDamageFactor(0.25f);
                setHitboxSize(0.5f * growth, 0.5f * growth);
                if (!this.level().isClientSide()) this.setPos(data.getTail2Pos().toVec3());
            }
        }
    }

    public int getPlayerId() {
        return this.entityData.get(PLAYER_ID);
    }

    public Player getPlayer() {
        return (Player) this.level().getEntity(getPlayerId());
    }

    public void setPlayerId(int val) {
        this.entityData.set(PLAYER_ID, val);
    }

    public float getDamageFactor() {
        return this.entityData.get(DAMAGE_FACTOR);
    }

    public void setDamageFactor(float val) {
        this.entityData.set(DAMAGE_FACTOR, val);
    }

    public void setHitboxSize(float width, float height) {
        this.entityData.set(WIDTH, width);
        this.entityData.set(HEIGHT, height);
        refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.entityData.get(WIDTH), this.entityData.get(HEIGHT));
    }

    private float getHitboxPos() {
        return 0f;
    }

    public void setHitboxType(String val) {
        this.entityData.set(TYPE, val.toUpperCase());
    }

    public String getHitboxType() {
        return this.entityData.get(TYPE).toUpperCase();
    }

}
