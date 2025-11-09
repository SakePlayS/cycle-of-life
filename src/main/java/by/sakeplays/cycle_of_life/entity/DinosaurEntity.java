package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
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

    public float prevBodyRot;
    public float playerRotDeltaOld = 0;
    public float playerRotDelta = 0;

    public DinosaurEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public List<Float> rotHistory = new ArrayList<>();
    public List<Float> tailYawHistory = new ArrayList<>();

    public List<Float> tailRotXHistory = new ArrayList<>();
    public List<Float> tailRotYHistory = new ArrayList<>();
    public List<Position> tailPosHistory = new ArrayList<>();
    public float prevRotY = 0;
    public float scale = 1;
    public int lastUpdatedTick = 0;
    public Position tailRootPos = new Position(0, 0, 0);
    public Position oldTailRootPos = new Position(0, 0, 0);

    public float tailRotX;
    public float tailRotY;
    public float tailYaw;

    public float headRot = 0;

    public SelectedColors colors = new SelectedColors();

    public volatile Integer playerId = 0;
    public boolean isForScreenRendering = false;
    protected int attackAnimUntil = 0;

    public void recordTailRotXHistory(int withSize) {
        if (tailRotXHistory.size() >= withSize) tailRotXHistory.removeFirst();

        tailRotXHistory.add(tailRotX);
    }

    public void recordTailRotYHistory(int withSize) {
        if (tailRotYHistory.size() >= withSize) tailRotYHistory.removeFirst();

        tailRotYHistory.add(tailRotY);
    }

    public void recordTailYawHistory(int withSize) {
        if (tailYawHistory.size() >= withSize) tailYawHistory.removeFirst();

        tailYawHistory.add((float) Math.asin(Math.sin(tailYaw)));
    }

    public void recordTailPosHistory(int withSize, Position position) {
        if (tailPosHistory.size() >= withSize) tailPosHistory.removeFirst();

        tailPosHistory.add(position);
    }



    // ALL synced data here is used ONLY for corpses!

    public static final EntityDataAccessor<Boolean> IS_CORPSE =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> IS_MALE =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Float> BODY_GROWTH =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> BODY_ROT =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> REMAINING_WEIGHT =
            SynchedEntityData.defineId(DinosaurEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<SelectedColors> COLORS =
            SynchedEntityData.defineId(DinosaurEntity.class, ModEntityDataSerializers.SELECTED_COLORS);

    public abstract SelectedColors getDefaultColors();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(IS_CORPSE, false);
        builder.define(IS_MALE, true);
        builder.define(BODY_GROWTH, 1f);
        builder.define(BODY_ROT, 0f);
        builder.define(REMAINING_WEIGHT, 1f);
        builder.define(COLORS, getDefaultColors());

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        setCorpse(compound.getBoolean("IsBody"));
        setMale(compound.getBoolean("IsMale"));
        setBodyGrowth(compound.getFloat("BodyGrowth"));
        setBodyRot(compound.getFloat("BodyRot"));

        setRemainingFood(compound.getFloat("RemainingWeight"));

        this.setColors(SelectedColors.fromNBT(compound.getCompound("SelectedColors")));

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("IsBody", isCorpse());
        compound.putBoolean("IsMale", isMale());
        compound.putFloat("BodyGrowth", getBodyGrowth());
        compound.putFloat("BodyRot", getBodyRot());

        compound.putFloat("RemainingWeight", getRemainingFood());

        CompoundTag colors = new CompoundTag();

        this.getColors().toNBT(colors);
        compound.put("SelectedColors", colors);

    }

    public Player getPlayer() {

        if (isCorpse()) {
            return null;
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

        if (getRemainingFood() <= 0) this.remove(RemovalReason.DISCARDED);
    }

    public boolean isCorpse() {
        return this.entityData.get(IS_CORPSE);
    }

    public void setCorpse(boolean val) {
        this.entityData.set(IS_CORPSE, val);
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

    public void setRemainingFood(float val) {
        this.entityData.set(REMAINING_WEIGHT, val);
    }

    public float getRemainingFood() {
        return this.entityData.get(REMAINING_WEIGHT);
    }

    public void setBodyRot(float val) {
        this.entityData.set(BODY_ROT, val);
    }

    public float getBodyRot() {
        return this.entityData.get(BODY_ROT);
    }

    public SelectedColors getColors() {
        return this.entityData.get(COLORS).copy();
    }

    public void setColors(SelectedColors colors) {
        this.entityData.set(COLORS, colors.copy());
    }

    public void recordRotHistory(float val, int withSize) {
        rotHistory.add(val);

        if (rotHistory.size() > withSize) {
            rotHistory.removeFirst();

            playerRotDeltaOld = playerRotDelta;
            playerRotDelta = rotHistory.getLast() - rotHistory.getFirst();
        }
    }

    public abstract Dinosaurs getDinosaurSpecies();

    public abstract DinosaurFood getMeatType();

}
