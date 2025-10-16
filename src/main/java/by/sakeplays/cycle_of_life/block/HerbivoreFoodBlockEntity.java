package by.sakeplays.cycle_of_life.block;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.entity.util.Diet;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public abstract class HerbivoreFoodBlockEntity extends BlockEntity {

    private int tickCount = 0;
    private int respawnTicks = 0;
    private float remainingFood = baseFoodAmount();
    private float xOffset = (float) (Math.random() * xModelOffset() * Math.signum(Math.random() - 0.5f));
    private float zOffset = (float) (Math.random() * zModelOffset() * Math.signum(Math.random() - 0.5f));
    private float rotationOffset = (float) (Math.random() * rotationModelOffset() * Math.signum(Math.random() - 0.5f));
    private float sizeOffset = (float) (Math.random() * sizeOffset() * Math.signum(Math.random() - 0.5f));
    private boolean shouldTickRespawnTimer = true;

    public HerbivoreFoodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.remainingFood = tag.getFloat("RemainingFood");

        this.xOffset = tag.getFloat("xModelOffset");
        this.zOffset = tag.getFloat("zModelOffset");
        this.rotationOffset = tag.getFloat("rotationModelOffset");
        this.sizeOffset = tag.getFloat("sizeOffset");
        this.respawnTicks = tag.getInt("RespawnTicks");

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("RemainingFood", this.remainingFood);

        tag.putFloat("xModelOffset", this.xOffset);
        tag.putFloat("zModelOffset", this.zOffset);
        tag.putFloat("rotationModelOffset", this.rotationOffset);
        tag.putFloat("sizeOffset", this.sizeOffset);
        tag.putInt("RespawnTicks", respawnTicks);

    }


    public float getRemainingFood() {
        return remainingFood;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public void serverTick(Level level, BlockPos pos, BlockState st) {
        tickCount++;

        if (tickCount % 50 == 0) shouldTickRespawnTimer = shouldRespawn();

        if (getRemainingFood() <= 0) {
            if (shouldTickRespawnTimer) respawnTicks++;
        }


        if (respawnTicks >= ticksToRespawn() && shouldTickRespawnTimer) {
            respawnTicks = 0;
            remainingFood = baseFoodAmount();
            sync();
        }
    }

    public abstract float rotationModelOffset();

    public abstract float xModelOffset();

    public abstract float zModelOffset();

    public float getXOffset() {
        return xOffset;
    }

    public float getZOffset() {
        return zOffset;
    }

    public float getRotationOffset() {
        return rotationOffset;
    }

    public float getSizeOffset() {
        return sizeOffset;
    }

    public abstract float sizeOffset();

    public abstract float baseFoodAmount();

    public abstract int ticksToRespawn();

    public abstract DinosaurFood foodType();

    public abstract boolean shouldRespawn();

    public boolean takePiece(Player player, float amount) {
        if (Util.getDino(player).getDiet() == Diet.CARNIVORE) return false;
        if (remainingFood <= 0) return false;

        player.getData(DataAttachments.HELD_FOOD_DATA).setHeldFoodItem(foodType());
        player.getData(DataAttachments.HELD_FOOD_DATA).setFoodWeight(Math.min(amount, remainingFood));
        remainingFood -= amount;

        sync();
        return true;
    }


    public void sync() {
        if (level != null && !level.isClientSide) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }


    protected boolean isPlayerNearby(double radius) {
        if (level == null) return false;
        Player nearest = level.getNearestPlayer(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, radius, false);
        return nearest != null;
    }
}
