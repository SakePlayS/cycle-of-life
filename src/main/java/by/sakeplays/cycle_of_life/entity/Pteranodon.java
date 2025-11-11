package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Pteranodon extends DinosaurEntity implements GeoEntity {


    public float airbrakeFactor = 1;


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation GLIDE = RawAnimation.begin().thenPlay("glide");
    protected static final RawAnimation FLAP = RawAnimation.begin().thenPlay("flap");
    protected static final RawAnimation FLAP_FAST = RawAnimation.begin().thenPlay("flap_fast");
    protected static final RawAnimation AIRBRAKING = RawAnimation.begin().thenPlay("airbrake");


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
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));

    }

    protected PlayState movementController(final AnimationState<Pteranodon> state) {

        if (getPlayer() != null) {

            Player player = getPlayer();
            DinoData data = player.getData(DataAttachments.DINO_DATA);
            float xzDelta = new Vec2((float)(player.getX() - player.xOld), (float)(player.getZ() - player.zOld)).length();

            if (data.isFlying()) {
                state.getController().transitionLength(8);

                if (data.isAirbraking())  return state.setAndContinue(AIRBRAKING);
                if ((xzDelta < 0.6f) || player.getY() - player.yOld > 0.15f || data.isSprinting()) return state.setAndContinue(FLAP_FAST);
                if (player.getY() - player.yOld > 0f) return state.setAndContinue(FLAP);

                return state.setAndContinue(GLIDE);
            }
            return state.setAndContinue(IDLE);

        }

        return state.setAndContinue(IDLE);
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
