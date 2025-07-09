package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Deinonychus extends DinosaurEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("deinonychus.walking");
    public static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("deinonychus.running");
    public static final RawAnimation SLIDE = RawAnimation.begin().thenLoop("deinonychus.slide");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("deinonychus.idle");

    protected static final RawAnimation REST_IN = RawAnimation.begin().thenPlay("deinonychus.resting_in");
    protected static final RawAnimation REST_LOOP = RawAnimation.begin().thenPlay("deinonychus.resting");
    protected static final RawAnimation REST_OUT = RawAnimation.begin().thenPlay("deinonychus.resting_out");

    protected static final RawAnimation SLASH = RawAnimation.begin().thenPlay("deinonychus.slash");
    protected static final RawAnimation TURNAROUND_SLASH = RawAnimation.begin().thenPlay("deinonychus.turnaround_slash");

    public float prevRotY = 0;
    public float prevTailRotY = 0;
    public float prevTailRotX = 0;

    public Deinonychus(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackController)
                .triggerableAnim("slash", SLASH)
                .triggerableAnim("turnaround_slash", TURNAROUND_SLASH)
                .triggerableAnim("rest_in", REST_IN)
                .triggerableAnim("rest_out", REST_OUT)
                .triggerableAnim("rest_loop", REST_LOOP)
        );
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


    protected PlayState movementController(final AnimationState<Deinonychus> state) {

        if (getPlayer() != null) {
            Player player = getPlayer();
            if (player.getData(DataAttachments.DINO_DATA).isSprinting() && player.getData(DataAttachments.DINO_DATA).isMoving()) {
                return state.setAndContinue(RUN_ANIM);
            }

            if (player.getData(DataAttachments.DINO_DATA).isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }

            if (player.getData(DataAttachments.DINO_DATA).isSliding()) {
                return state.setAndContinue(SLIDE);
            }

            return state.setAndContinue(IDLE);

        }
        return PlayState.STOP;
    }

    protected PlayState attackController(final AnimationState<Deinonychus> state) {

        return PlayState.CONTINUE;

    }


}
