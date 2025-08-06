package by.sakeplays.cycle_of_life.entity;


import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Pachycephalosaurus extends DinosaurEntity implements GeoEntity {

    protected static final RawAnimation BASH = RawAnimation.begin().thenPlay("pachycephalosaurus.bash");
    protected static final RawAnimation UPPER_BASH = RawAnimation.begin().thenPlay("pachycephalosaurus.upper_bash");

    protected static final RawAnimation REST_IN = RawAnimation.begin().thenPlay("pachycephalosaurus.resting_in");
    protected static final RawAnimation REST_LOOP = RawAnimation.begin().thenPlay("pachycephalosaurus.resting");
    protected static final RawAnimation REST_OUT = RawAnimation.begin().thenPlay("pachycephalosaurus.resting_out");

    protected static final RawAnimation KNOCKED_DOWN = RawAnimation.begin().thenLoop("pachycephalosaurus.knocked_down");

    protected static final RawAnimation NONE = RawAnimation.begin().thenLoop("pachycephalosaurus.none");
    protected static final RawAnimation CHARGE_ANIM = RawAnimation.begin().thenLoop("pachycephalosaurus.charge");
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("pachycephalosaurus.run");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("pachycephalosaurus.walk");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("pachycephalosaurus.idle");
    public float prevRotY = 0;


    public Pachycephalosaurus(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);

        bellyColor = new ColorOption(206, 191, 153, 1f, 0.3f).toInt();
        maleDisplayColor = new ColorOption(78, 91, 78, 1f, 0.89f).toInt();
        flankColor = new ColorOption(81, 70, 66, 1f, 0.5f).toInt();
        bodyColor = new ColorOption(44, 36, 36, 1f, 0.7f).toInt();
        markingsColor = new ColorOption(140, 100, 82, 1f, 0.5f).toInt();

    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));
        controllers.add(new AnimationController<>(this, "charge", 5, this::chargeController));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackController)
                .triggerableAnim("upper_bash", UPPER_BASH)
                .triggerableAnim("rest_in", REST_IN)
                .triggerableAnim("rest_out", REST_OUT)
                .triggerableAnim("rest_loop", REST_LOOP)
                .triggerableAnim("bash", BASH));

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
            DinoData data = player.getData(DataAttachments.DINO_DATA);

            if (player.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return state.setAndContinue(KNOCKED_DOWN);


            if (data.isMoving() && data.isSprinting()) return state.setAndContinue(RUN_ANIM);
            if (data.isMoving()) return state.setAndContinue(WALK_ANIM);
            return state.setAndContinue(IDLE);

        }

        return state.setAndContinue(IDLE);
    }

    protected PlayState attackController(final AnimationState<Pachycephalosaurus> state) {
        return PlayState.CONTINUE;
    }

    protected PlayState chargeController(final AnimationState<Pachycephalosaurus> state) {

        if (getPlayer().getData(DataAttachments.DINO_DATA).isCharging()) return state.setAndContinue(CHARGE_ANIM);

        return state.setAndContinue(NONE);
    }
}
