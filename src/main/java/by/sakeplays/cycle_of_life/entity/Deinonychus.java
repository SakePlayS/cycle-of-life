package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.core.BlockPos;
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

    protected static final RawAnimation REST_IN = RawAnimation.begin().thenPlay("deinonychus.resting_in");
    protected static final RawAnimation REST_LOOP = RawAnimation.begin().thenPlay("deinonychus.resting");
    protected static final RawAnimation REST_OUT = RawAnimation.begin().thenPlay("deinonychus.resting_out");

    protected static final RawAnimation COURTING_MALE = RawAnimation.begin().thenPlay("deinonychus.courting_male");

    protected static final RawAnimation SLASH = RawAnimation.begin().thenPlay("deinonychus.slash");
    protected static final RawAnimation TURNAROUND_SLASH = RawAnimation.begin().thenPlay("deinonychus.turnaround_slash");

    public float prevRotY = 0;
    public float prevTailRotY = 0;
    public float prevTailRotX = 0;

    public int eyesColor = Util.rgbaToInt(0.45f, 0.65f, 0.95f, 1f);
    public int bodyColor = Util.rgbaToInt(0.2f, 0.2f, 0.33f, 1f);
    public int flankColor = Util.rgbaToInt(0.3f, 0.3f, 0.4f, 1f);
    public int markingsColor = Util.rgbaToInt(0.12f, 0.15f, 0.25f, 1f);
    public int bellyColor = Util.rgbaToInt(0.55f, 0.8f, 0.9f, 1f);
    public int maleDisplayColor = Util.rgbaToInt(0.9f, 0.35f, 0.42f, 1f);



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
                .triggerableAnim("courting_male", COURTING_MALE)
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

    @Override
    public boolean shouldShowName() {
        return false;
    }

    protected PlayState movementController(final AnimationState<Deinonychus> state) {

        if (getPlayer() != null) {
            Player player = getPlayer();

            if (player.isInWater()) {
                return state.setAndContinue(SWIM_SLOW);
            }

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
