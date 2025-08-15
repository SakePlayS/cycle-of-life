package by.sakeplays.cycle_of_life.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class DeinonychusNestBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int eggsCount = 0;
    private int oldEggsCount = 0;


    public DeinonychusNestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DEINONYCHUS_NEST_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.eggsCount = tag.getInt("EggsCount");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("EggsCount", this.eggsCount);

    }

    public void serverTick(Level level, BlockPos pos, BlockState st) {
        List<ServerPlayer> players = ((ServerLevel) level).players();


        if (oldEggsCount != eggsCount) updateNearbyPlayers(level);

        oldEggsCount = eggsCount;
    }


    private void updateNearbyPlayers(Level level) {
        List<ServerPlayer> players = ((ServerLevel) level).players();
    }


}
