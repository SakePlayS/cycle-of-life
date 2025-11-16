package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class Pteranodon extends DinosaurEntity implements GeoEntity {


    public float airbrakeFactor = 1;


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation GLIDE = RawAnimation.begin().thenPlay("glide");
    protected static final RawAnimation FLAP = RawAnimation.begin().thenPlay("flap");
    protected static final RawAnimation FLAP_FAST = RawAnimation.begin().thenPlay("flap_fast");
    protected static final RawAnimation AIRBRAKING = RawAnimation.begin().thenPlay("airbrake");
    protected static final RawAnimation PECK_FLIGHT = RawAnimation.begin().thenPlay("peck_flying");
    protected static final RawAnimation ATTACK_NONE = RawAnimation.begin().thenPlay("attack_none");

    public List<Float> yMomentumHistory = new ArrayList<>();
    public float legPos = 0;
    public float legPosOld = 0;

    public float legRotX1 = 0;
    public float legRotX2 = 0;


    public float targetLegRotX1 = 0;
    public float targetLegRotX2 = 0;

    public void recordYMomentumHistory(float val) {
        yMomentumHistory.add(val);
        if (yMomentumHistory.size() >15) yMomentumHistory.removeFirst();
    }

    public void clientRenderTick() {

        recordRotHistory(getPlayer().getData(DataAttachments.PLAYER_ROTATION), 2);


        recordYMomentumHistory(legPos - legPosOld);
        legPosOld = legPos;

    }

    public Pteranodon(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public SelectedColors getDefaultColors() {
        SelectedColors colors = new SelectedColors();

        colors.setColor(
                ColorableBodyParts.EYES,
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.EYES).getFirst().first().toInt(),
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.EYES).getFirst().second().toInt()
        );
        colors.setColor(
                ColorableBodyParts.BODY,
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.BODY).getFirst().first().toInt(),
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.BODY).getFirst().second().toInt()
        );
        colors.setColor(
                ColorableBodyParts.MARKINGS,
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.MARKINGS).getFirst().first().toInt(),
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.MARKINGS).getFirst().second().toInt()
        );
        colors.setColor(
                ColorableBodyParts.BELLY,
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.BELLY).getFirst().first().toInt(),
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.BELLY).getFirst().second().toInt()
        );
        colors.setColor(
                ColorableBodyParts.MALE_DISPLAY,
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.MALE_DISPLAY).getFirst().first().toInt(),
                Dinosaurs.DEINONYCHUS.getColorOptions().getColorOptions().get(ColorableBodyParts.MALE_DISPLAY).getFirst().second().toInt()
        );
        return colors;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController).setSoundKeyframeHandler(state -> {
            Player player = getPlayer();

            if (player != null) {
                player.level().playSound(player, player.getOnPos(), SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 1.5f, 1.5f);
            }
        }));

        controllers.add(new AnimationController<>(this, "attack", 0, this::attackController).setSoundKeyframeHandler(state -> {
            Player player = getPlayer();

            if (player != null) {
                player.level().playSound(player, player.getOnPos(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5f, 2f);
                player.level().playSound(player, player.getOnPos(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.5f, 2f);

            }
        }));

    }

    private int flapSoundTick = 0;
    private boolean flapSoundPlayed = false;
    protected PlayState movementController(final AnimationState<Pteranodon> state) {

        if (getPlayer() != null) {

            Player player = getPlayer();
            DinoData data = player.getData(DataAttachments.DINO_DATA);
            float xzDelta = new Vec2((float)(player.getX() - player.xOld), (float)(player.getZ() - player.zOld)).length();

            if (data.isFlying()) {

                state.getController().transitionLength(8);

                if (data.isAirbraking()) {
                    return state.setAndContinue(AIRBRAKING);
                }
                if ((xzDelta < 0.6f) || player.getY() - player.yOld > 0.15f || data.isSprinting()) {
                    return state.setAndContinue(FLAP_FAST);
                }
                if (player.getY() - player.yOld > 0f) return state.setAndContinue(FLAP);

                return state.setAndContinue(GLIDE);
            }
            return state.setAndContinue(IDLE);

        }

        return state.setAndContinue(IDLE);
    }

    private int tickToResetAttack = 0;
    protected PlayState attackController(final AnimationState<Pteranodon> state) {

        if (getPlayer() != null) {

            DinoData data = getPlayer().getData(DataAttachments.DINO_DATA);

            if (getPlayer().getData(DataAttachments.ATTACK_MAIN_1) && data.isFlying()) {
                tickToResetAttack = getPlayer().tickCount + 20;
                return state.setAndContinue(PECK_FLIGHT);
            }

            if (getPlayer().tickCount > tickToResetAttack) return state.setAndContinue(ATTACK_NONE);


        }

        return PlayState.CONTINUE;
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        if (playerId != null && !isCorpse()) {
            return level().getEntity(playerId).tickCount;
        }

        return GeoEntity.super.getTick(object);
    }

    @Override
    public Dinosaurs getDinosaurSpecies() {
        return Dinosaurs.PTERANODON;
    }

    @Override
    public DinosaurFood getMeatType() {
        return DinosaurFood.DEINONYCHUS_MEAT;
    }


}
