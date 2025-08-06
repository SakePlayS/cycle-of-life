package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.Position;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class DinosaurEntity extends LivingEntity {

    public DinosaurEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public float prevRotY = 0;
    public float prevTailRotY1 = 0;
    public float prevTailRotY2 = 0;
    public float prevTailRotY3 = 0;
    public float scale = 1;

    public float prevTailRotX = 0;

    public List<Position> tailBonePositionHistory = new ArrayList<>();

    public float headRot = 0;

    public int eyesColor = Util.rgbaToInt(0.45f, 0.65f, 0.95f, 1f);
    public int bodyColor = Util.rgbaToInt(0.2f, 0.2f, 0.33f, 1f);
    public int flankColor = Util.rgbaToInt(0.3f, 0.3f, 0.4f, 1f);
    public int markingsColor = Util.rgbaToInt(0.12f, 0.15f, 0.25f, 1f);
    public int bellyColor = Util.rgbaToInt(0.55f, 0.8f, 0.9f, 1f);
    public int maleDisplayColor = Util.rgbaToInt(0.9f, 0.35f, 0.42f, 1f);
    public volatile Integer playerId = 0;
    public boolean isForScreenRendering = false;

    // ALL synched data here is used ONLY for dead bodies!

    public static final EntityDataAccessor<Boolean> IS_BODY =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> IS_MALE =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Float> BODY_GROWTH =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> BODY_ROT =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> REMAINING_WEIGHT =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Integer> OLD_PLAYER =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BODY_FLANK_COLOR =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BODY_BELLY_COLOR =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BODY_MARKINGS_COLOR =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BODY_BODY_COLOR =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BODY_EYES_COLOR =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BODY_MALE_DISPLAY_COLOR =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(IS_BODY, false);
        builder.define(IS_MALE, true);
        builder.define(BODY_GROWTH, 1f);
        builder.define(BODY_ROT, 0f);

        builder.define(OLD_PLAYER, 0);

        builder.define(BODY_MALE_DISPLAY_COLOR, 0);
        builder.define(BODY_BODY_COLOR, 0);
        builder.define(BODY_EYES_COLOR, 0);
        builder.define(BODY_BELLY_COLOR, 0);
        builder.define(BODY_FLANK_COLOR, 0);
        builder.define(BODY_MARKINGS_COLOR, 0);
        builder.define(REMAINING_WEIGHT, 1f);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        setBody(compound.getBoolean("IsBody"));
        setMale(compound.getBoolean("IsMale"));
        setBodyGrowth(compound.getFloat("BodyGrowth"));
        setBodyRot(compound.getFloat("BodyRot"));

        setRemainingWeight(compound.getFloat("RemainingWeight"));

        setOldPlayer(compound.getInt("OldPlayer"));

        setBodyColor(compound.getInt("BodyColor"));
        setFlankColor(compound.getInt("FlankColor"));
        setMaleDisplayColor(compound.getInt("MaleDisplayColor"));
        setMarkingsColor(compound.getInt("MarkingsColor"));
        setBellyColor(compound.getInt("BellyColor"));
        setEyesColor(compound.getInt("EyesColor"));

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("IsBody", isBody());
        compound.putBoolean("IsMale", isMale());
        compound.putFloat("BodyGrowth", getBodyGrowth());
        compound.putFloat("BodyRot", getBodyRot());

        compound.putFloat("RemainingWeight", getRemainingWeight());

        compound.putInt("OldPlayer", getOldPlayerID());

        compound.putInt("BodyColor", getBodyColor());
        compound.putInt("FlankColor", getFlankColor());
        compound.putInt("EyesColor", getEyesColor());
        compound.putInt("MarkingsColor", getMarkingsColor());
        compound.putInt("BellyColor", getBellyColor());
        compound.putInt("MaleDisplayColor", getMaleDisplayColor());


    }

    public Player getPlayer() {

        if (isBody()) {
            if (level().getEntity(getOldPlayerID()) instanceof Player player) return player;
        }

        if (level().getEntity(playerId) instanceof Player player && playerId != null) {

            return player;
        }

        return null;
    }


    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        Player player = getPlayer();

        if (player != null) {
            return player.getItemBySlot(equipmentSlot);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        Player player = getPlayer();

        if (player != null) {
            player.setItemSlot(equipmentSlot, itemStack);
        }
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void tick() {
        super.tick();

        if (getRemainingWeight() <= 0) this.remove(RemovalReason.DISCARDED);
    }

    public boolean isBody() {
        return this.entityData.get(IS_BODY);
    }

    public void setBody(boolean val) {
        this.entityData.set(IS_BODY, val);
    }

    public boolean isMale() {
        return this.entityData.get(IS_MALE);
    }

    public void setMale(boolean val) {
        this.entityData.set(IS_MALE, val);
    }

    public void setBodyGrowth(float val) {
        this.entityData.set(BODY_GROWTH, val);
    }

    public float getBodyGrowth() {
        return this.entityData.get(BODY_GROWTH);
    }

    public void setRemainingWeight(float val) {
        this.entityData.set(REMAINING_WEIGHT, val);
    }

    public float getRemainingWeight() {
        return this.entityData.get(REMAINING_WEIGHT);
    }

    public void setBodyRot(float val) {
        this.entityData.set(BODY_ROT, val);
    }

    public float getBodyRot() {
        return this.entityData.get(BODY_ROT);
    }

    public void setOldPlayer(int val) {
        this.entityData.set(OLD_PLAYER, val);
    }

    public int getOldPlayerID() {
        return this.entityData.get(OLD_PLAYER);
    }

    public void setBodyColor(int val) {
        this.entityData.set(BODY_BODY_COLOR, val);
    }

    public int getBodyColor() {
        return this.entityData.get(BODY_BODY_COLOR);
    }

    public void setMaleDisplayColor(int val) {
        this.entityData.set(BODY_MALE_DISPLAY_COLOR, val);
    }

    public int getMaleDisplayColor() {
        return this.entityData.get(BODY_MALE_DISPLAY_COLOR);
    }

    public void setMarkingsColor(int val) {
        this.entityData.set(BODY_MARKINGS_COLOR, val);
    }

    public int getMarkingsColor() {
        return this.entityData.get(BODY_MARKINGS_COLOR);
    }

    public void setFlankColor(int val) {
        this.entityData.set(BODY_FLANK_COLOR, val);
    }

    public int getFlankColor() {
        return this.entityData.get(BODY_FLANK_COLOR);
    }

    public void setBellyColor(int val) {
        this.entityData.set(BODY_BELLY_COLOR, val);
    }

    public int getBellyColor() {
        return this.entityData.get(BODY_BELLY_COLOR);
    }

    public void setEyesColor(int val) {
        this.entityData.set(BODY_EYES_COLOR, val);
    }

    public int getEyesColor() {
        return this.entityData.get(BODY_EYES_COLOR);
    }

    public void recordTailPosHistory(Position pos) {
        tailBonePositionHistory.add(pos);

        if (tailBonePositionHistory.size() > 11) tailBonePositionHistory.removeFirst();
    }




}
