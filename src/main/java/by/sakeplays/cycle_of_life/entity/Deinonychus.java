package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Deinonychus extends DinosaurEntity implements GeoEntity {

    private int jumpAnimUntil = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("deinonychus.walking");
    public static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("deinonychus.running");
    public static final RawAnimation SLIDE = RawAnimation.begin().thenLoop("deinonychus.slide");
    public static final RawAnimation SWIM_SLOW = RawAnimation.begin().thenLoop("deinonychus.swim");

    protected static final RawAnimation AIRBORNE = RawAnimation.begin().thenLoop("deinonychus.airborne");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("deinonychus.idle");
    protected static final RawAnimation IDLE_WINDUP = RawAnimation.begin().thenLoop("deinonychus.idle_windup");
    protected static final RawAnimation ATTACK_BASE = RawAnimation.begin().thenLoop("deinonychus.attack_base");
    protected static final RawAnimation LAYING_EGGS = RawAnimation.begin().thenLoop("deinonychus.laying_eggs");

    protected static final RawAnimation BLINK = RawAnimation.begin().thenPlay("deinonychus.blink");
    protected static final RawAnimation HOLD_ITEM = RawAnimation.begin().thenPlay("deinonychus.item_hold");
    protected static final RawAnimation FOOD_SWALLOW = RawAnimation.begin().thenPlay("deinonychus.item_consume");
    protected static final RawAnimation ITEM_NOTHING = RawAnimation.begin().thenPlay("deinonychus.item_nothing");

    protected static final RawAnimation LAND = RawAnimation.begin().thenPlay("deinonychus.land");
    protected static final RawAnimation JUMP = RawAnimation.begin().thenPlay("deinonychus.jump");
    protected static final RawAnimation JUMP_MOVING = RawAnimation.begin().thenPlay("deinonychus.jump_moving");
    protected static final RawAnimation JUMP_SPRINTING = RawAnimation.begin().thenPlay("deinonychus.jump_sprinting");
    protected static final RawAnimation JUMP_WINDUP = RawAnimation.begin().thenPlay("deinonychus.jump_windup");
    protected static final RawAnimation JUMP_IDLE = RawAnimation.begin().thenPlay("deinonychus.jump_idle");

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


    }

    @Override
    public SelectedColors getDefaultColors() {
        SelectedColors colors = new SelectedColors();

        colors.setColor(ColorableBodyParts.EYES, Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.EYES).getFirst().toInt());
        colors.setColor(ColorableBodyParts.BODY, Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.BODY).getFirst().toInt());
        colors.setColor(ColorableBodyParts.MARKINGS, Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.MARKINGS).getFirst().toInt());
        colors.setColor(ColorableBodyParts.BELLY, Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.BELLY).getFirst().toInt());
        colors.setColor(ColorableBodyParts.MALE_DISPLAY, Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.MALE_DISPLAY).getFirst().toInt());

        return colors;
    }

    @Override
    public Dinosaurs getDinosaurSpecies() {
        return Dinosaurs.DEINONYCHUS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));
        controllers.add(new AnimationController<>(this, "jump", 5, this::jumpController));
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

        controllers.add(new AnimationController<>(this, "hold", 2, this::holdItemController)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public double getTick(Object object) {
        if (playerId != null && !isCorpse()) {

            return getPlayer().tickCount;
        }

        return GeoEntity.super.getTick(object);
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    private boolean jumpReturned = false;
    protected PlayState movementController(final AnimationState<Deinonychus> state) {

        if (isCorpse()) return state.setAndContinue(DEAD);
        if (isForScreenRendering) return state.setAndContinue(IDLE);

        if (getPlayer() != null) {
            Player player = getPlayer();

            DinoData data = player.getData(DataAttachments.DINO_DATA);
            float currSpeed = new Vec2((float) (player.getX() - player.xOld), (float) (player.getZ() - player.zOld)).length();
            float maxWalkSpeed = getDinosaurSpecies().getWalkSpeed() *
                    getDinosaurSpecies().getGrowthCurve().calculate(data.getGrowth(), GrowthCurveStat.SPEED);
            float maxSprintSpeed = getDinosaurSpecies().getSprintSpeed() *
                    getDinosaurSpecies().getGrowthCurve().calculate(data.getGrowth(), GrowthCurveStat.SPEED);

            state.getController().transitionLength(5);
            Util.setAnimationSpeed(1, state.animationTick, state.getController());

            if (player.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return state.setAndContinue(DEAD);

            if (player.getData(DataAttachments.JUMP_ANIM_FLAG) || player.tickCount < jumpAnimUntil) {
                state.getController().transitionLength(3);
                if (player.getData(DataAttachments.JUMP_ANIM_FLAG)) jumpAnimUntil = player.tickCount + 10;
                player.setData(DataAttachments.JUMP_ANIM_FLAG, false);

                if (jumpReturned) return PlayState.CONTINUE;
                jumpReturned = true;

                if (player.getData(DataAttachments.DINO_DATA).isSprinting()) return state.setAndContinue(JUMP_SPRINTING);
                if (player.getData(DataAttachments.DINO_DATA).isMoving()) return state.setAndContinue(JUMP_MOVING);
                return state.setAndContinue(JUMP);
            }

            jumpReturned = false;

            if (!player.onGround() && !player.isInWater()) {
                state.getController().transitionLength(12);
                return state.setAndContinue(AIRBORNE);
            }

            if (player.getData(DataAttachments.DINO_DATA).isDrinking()) {
                state.getController().transitionLength(5);
                return state.setAndContinue(DRINK);
            }

            if (player.isInWater()) {
                return state.setAndContinue(SWIM_SLOW);
            }

            if (player.getData(DataAttachments.DINO_DATA).isSliding()) {
                return state.setAndContinue(SLIDE);
            }

            if ((data.isSprinting() && data.isMoving()) || currSpeed > maxWalkSpeed * 1.2) {

                float ratio = currSpeed/Math.max(0.01f, maxSprintSpeed);
                float baseAnimSpeed = getDinosaurSpecies().getGrowthCurve().calculate(data.getGrowth(), GrowthCurveStat.RUN_BASE_ANIM_SPEED);
                float target = ratio * baseAnimSpeed;

                Util.setAnimationSpeed(target, state.animationTick, state.getController());
                return state.setAndContinue(RUN_ANIM);
            }

            if (player.getData(DataAttachments.DINO_DATA).isMoving()) {

                float ratio = currSpeed/Math.max(0.01f, maxWalkSpeed);
                float baseAnimSpeed = getDinosaurSpecies().getGrowthCurve().calculate(data.getGrowth(), GrowthCurveStat.WALK_BASE_ANIM_SPEED);
                float target = ratio * baseAnimSpeed;

                Util.setAnimationSpeed(target, state.animationTick, state.getController());
                return state.setAndContinue(WALK_ANIM);
            }

            if (player.getData(DataAttachments.ALT_ATTACK)) attackAnimUntil = player.tickCount + 15;
            if (player.getData(DataAttachments.ATTACK_MAIN_1)) {
                attackAnimUntil = player.tickCount + 6;
            }
            if (player.getData(DataAttachments.ATTACK_MAIN_2)) attackAnimUntil = player.tickCount + 6;

            if (player.tickCount < attackAnimUntil) return state.setAndContinue(ATTACK_BASE);

            if (player.getData(DataAttachments.DINO_DATA).isLayingEggs()) return state.setAndContinue(LAYING_EGGS);

            if (player.getData(DataAttachments.JUMP_WINDUP)) {
                state.getController().transitionLength(12);
                return state.setAndContinue(IDLE_WINDUP);
            }

            return state.setAndContinue(IDLE);

        }
        return PlayState.STOP;
    }

    private int landAnimUntil = 0;
    private boolean wasOnGround = true;
    protected PlayState jumpController(final AnimationState<Deinonychus> state) {

        state.getController().transitionLength(3);

        if (isCorpse()) return state.setAndContinue(JUMP_IDLE);
        if (isForScreenRendering) return state.setAndContinue(JUMP_IDLE);

        if (getPlayer() != null) {

            Player player = getPlayer();

            if ((player.onGround() && !wasOnGround) || player.tickCount < landAnimUntil) {
                state.getController().transitionLength(0);
                if (player.onGround() && !wasOnGround) landAnimUntil = player.tickCount + 11;
                wasOnGround = player.onGround();
                return state.setAndContinue(LAND);
            }

            wasOnGround = player.onGround();

            if (player.getData(DataAttachments.JUMP_WINDUP)) return state.setAndContinue(JUMP_WINDUP);

        }

        return state.setAndContinue(JUMP_IDLE);
    }

    protected PlayState attackController(final AnimationState<Deinonychus> state) {

        return PlayState.CONTINUE;

    }

    protected PlayState blinkController(final AnimationState<Deinonychus> state) {

        return PlayState.CONTINUE;

    }

    protected PlayState holdItemController(final AnimationState<Deinonychus> state) {

        if (getPlayer() != null && !isCorpse()) {

            if (getPlayer().getData(DataAttachments.EATING_TIME) > 0) {
                return state.setAndContinue(FOOD_SWALLOW);
            }

            if (getPlayer().getData(DataAttachments.HELD_FOOD_DATA).getHeldFood() != DinosaurFood.FOOD_NONE) {
                return state.setAndContinue(HOLD_ITEM);
            }

        }


        return state.setAndContinue(ITEM_NOTHING);

    }

    @Override
    public DinosaurFood getMeatType() {
        return DinosaurFood.DEINONYCHUS_MEAT;
    }
}
