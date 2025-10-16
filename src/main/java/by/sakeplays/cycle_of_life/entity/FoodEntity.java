package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FoodEntity extends LivingEntity implements GeoEntity {

    public static final EntityDataAccessor<String> FOOD_TYPE =
            SynchedEntityData.defineId(FoodEntity.class, EntityDataSerializers.STRING);

    public static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(FoodEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FoodEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(FOOD_TYPE, DinosaurFood.DEINONYCHUS_MEAT.toString());
        builder.define(SIZE, 1f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        setRemainingFood(compoundTag.getFloat("Size"));
        setFoodType(DinosaurFood.fromString(compoundTag.getString("FoodType")));
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putFloat("Size", getRemainingFood());
        compoundTag.putString("FoodType", getFoodType().toString());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();
        refreshDimensions();
        setYRot(0);
        if (getRemainingFood() <= 0) this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    public void setRemainingFood(float value) {
        this.entityData.set(SIZE, value);
    }

    public float getRemainingFood() {
        return this.entityData.get(SIZE);
    }

    public void setFoodType(DinosaurFood foodType) {
        this.entityData.set(FOOD_TYPE, foodType.toString());
    }

    public DinosaurFood getFoodType() {
        return (DinosaurFood.fromString(this.entityData.get(FOOD_TYPE)));
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

}
