package by.sakeplays.cycle_of_life.entity;


import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Pachycephalosaurus extends DinosaurEntity implements GeoEntity {

    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("pachycephalosaurus.walking");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("pachycephalosaurus.idle");


    public Pachycephalosaurus(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        if (playerId != null) {
            return level().getEntity(playerId).tickCount;
        }
        return 0;
    }


    protected PlayState movementController(final AnimationState<Pachycephalosaurus> state) {

        if (getPlayer() != null) {

            Player player = getPlayer();

            if (player.getData(DataAttachments.DINO_DATA).isMoving())  {
                return state.setAndContinue(WALK_ANIM);
            }

            return state.setAndContinue(IDLE);

        }

        return PlayState.STOP;
    }


}
