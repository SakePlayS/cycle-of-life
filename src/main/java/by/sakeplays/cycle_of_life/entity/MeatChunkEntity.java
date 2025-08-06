package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MeatChunkEntity extends Entity implements GeoEntity {

    public static final EntityDataAccessor<Integer> CARRIER_ID =
            SynchedEntityData.defineId(MeatChunkEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Float> SIZE =
            SynchedEntityData.defineId(MeatChunkEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MeatChunkEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CARRIER_ID, 0);
        builder.define(SIZE, 1f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        setSize(compoundTag.getFloat("Size"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putFloat("Size", getSize());
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


        followMouth();
    }

    public void setCarrier(int carrier) {
        this.entityData.set(CARRIER_ID, carrier);
    }

    public int getCarrierId() {
        return this.entityData.get(CARRIER_ID);
    }

    public void setSize(float value) {
        this.entityData.set(SIZE, value);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public Entity getCarrier() {
        return this.level().getEntity(this.entityData.get(CARRIER_ID));
    }


    private void followMouth() {
        if (getCarrier() == null || getCarrierId() == 0) {
            return;
        }

        if (getCarrier() instanceof Player player) {
            Position pos = player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos();

        }
    }
}
