package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.util.Util;
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
    public static final RawAnimation SWIM_SLOW = RawAnimation.begin().thenLoop("deinonychus.swim");

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("deinonychus.idle");
    protected static final RawAnimation BLINK = RawAnimation.begin().thenPlay("deinonychus.blink");

    protected static final RawAnimation REST_IN = RawAnimation.begin().thenPlay("deinonychus.resting_in");
    protected static final RawAnimation REST_LOOP = RawAnimation.begin().thenPlay("deinonychus.resting");
    protected static final RawAnimation REST_OUT = RawAnimation.begin().thenPlay("deinonychus.resting_out");
    protected static final RawAnimation DRINK = RawAnimation.begin().thenPlay("deinonychus.drink_in")
            .thenLoop("deinonychus.drink");

    protected static final RawAnimation DEAD = RawAnimation.begin().thenPlay("deinonychus.dead");

    protected static final RawAnimation COURTING_FEMALE = RawAnimation.begin().thenPlay("deinonychus.courting_female");
    protected static final RawAnimation COURTING_MALE = RawAnimation.begin().thenPlay("deinonychus.courting_male");

    protected static final RawAnimation BITE = RawAnimation.begin().thenPlay("deinonychus.bite");
    protected static final RawAnimation SLASH_LEFT = RawAnimation.begin().thenPlay("deinonychus.slash_left");
    protected static final RawAnimation SLASH_RIGHT = RawAnimation.begin().thenPlay("deinonychus.slash_right");

    protected static final RawAnimation TURNAROUND_SLASH = RawAnimation.begin().thenPlay("deinonychus.turnaround_slash");





    public Deinonychus(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        eyesColor = Util.rgbaToInt(0.45f, 0.65f, 0.95f, 1f);
        bodyColor = Util.rgbaToInt(0.2f, 0.2f, 0.33f, 1f);
        flankColor = Util.rgbaToInt(0.3f, 0.3f, 0.4f, 1f);
        markingsColor = Util.rgbaToInt(0.12f, 0.15f, 0.25f, 1f);
        bellyColor = Util.rgbaToInt(0.55f, 0.8f, 0.9f, 1f);
        maleDisplayColor = Util.rgbaToInt(0.9f, 0.35f, 0.42f, 1f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackController)
                .triggerableAnim("bite", BITE)
                .triggerableAnim("turnaround_slash", TURNAROUND_SLASH)
                .triggerableAnim("slash_right", SLASH_LEFT)
                .triggerableAnim("slash_left", SLASH_RIGHT)
                .triggerableAnim("rest_in", REST_IN)
                .triggerableAnim("rest_out", REST_OUT)
                .triggerableAnim("rest_loop", REST_LOOP)
                .triggerableAnim("courting_male", COURTING_MALE)
                .triggerableAnim("courting_female", COURTING_FEMALE)

        );

        controllers.add(new AnimationController<>(this, "blink", 0, this::blinkController)
                .triggerableAnim("blink", BLINK)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        if (playerId != null && !isBody()) {
            return level().getEntity(playerId).tickCount;
        }

        return GeoEntity.super.getTick(object);
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    protected PlayState movementController(final AnimationState<Deinonychus> state) {

        if (isBody()) return state.setAndContinue(DEAD);


        if (getPlayer() != null) {
            Player player = getPlayer();

            state.getController().transitionLength(5);

            if (isForScreenRendering) return state.setAndContinue(IDLE);

            if (player.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return state.setAndContinue(DEAD);

            if (player.getData(DataAttachments.DINO_DATA).isDrinking()) {
                state.getController().transitionLength(0);
                return state.setAndContinue(DRINK);
            }

            if (player.isInWater()) {
                return state.setAndContinue(SWIM_SLOW);
            }

            if (player.getData(DataAttachments.DINO_DATA).isSliding()) {
                return state.setAndContinue(SLIDE);
            }

            if (player.getData(DataAttachments.DINO_DATA).isSprinting() && player.getData(DataAttachments.DINO_DATA).isMoving()) {
                return state.setAndContinue(RUN_ANIM);
            }

            if (player.getData(DataAttachments.DINO_DATA).isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }

            return state.setAndContinue(IDLE);

        }
        return PlayState.STOP;
    }

    protected PlayState attackController(final AnimationState<Deinonychus> state) {

        return PlayState.CONTINUE;

    }

    protected PlayState blinkController(final AnimationState<Deinonychus> state) {

        return PlayState.CONTINUE;

    }



}
