package by.sakeplays.cycle_of_life.block.herbi_food;

import by.sakeplays.cycle_of_life.block.HerbivoreFoodBlockEntity;
import by.sakeplays.cycle_of_life.block.ModBlockEntities;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class CrimsonweedBlockEntity extends HerbivoreFoodBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public CrimsonweedBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRIMSONWEED_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState st) {
        super.serverTick(level, pos, st);
    }

    @Override
    public float rotationModelOffset() {
        return 0.7f;
    }

    @Override
    public float xModelOffset() {
        return 4f;
    }

    @Override
    public float zModelOffset() {
        return 4f;
    }

    @Override
    public float sizeOffset() {
        return 0.2f;
    }

    @Override
    public float baseFoodAmount() {
        return 12f;
    }

    @Override
    public int ticksToRespawn() {
        return 40;
    }

    @Override
    public DinosaurFood foodType() {
        return DinosaurFood.CRIMSONWEED;
    }

    @Override
    public boolean shouldRespawn() {

        if (isPlayerNearby(64F)) return false;

        return true;
    }
}
