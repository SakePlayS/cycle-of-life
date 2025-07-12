package by.sakeplays.cycle_of_life.client.entity.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;

public class DeinonychusModel extends GeoModel<Deinonychus> {


    @Override
    public ResourceLocation getModelResource(Deinonychus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/deinonychus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Deinonychus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/entity/deinonychus/deinonychus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Deinonychus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "animations/deinonychus.animation.json");
    }

    @Override
    public void setCustomAnimations(Deinonychus animatable, long instanceId, AnimationState<Deinonychus> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animatable.isForScreenRendering) return;


        if (animatable.getPlayer().getData(DataAttachments.ATTACK_MAIN_1)) animatable.triggerAnim("attack", "slash");
        if (animatable.getPlayer().getData(DataAttachments.ATTACK_TURNAROUND)) animatable.triggerAnim("attack", "turnaround_slash");
        if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 1) animatable.triggerAnim("attack", "rest_in");
        if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 2) animatable.triggerAnim("attack", "rest_loop");
        if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 3) animatable.triggerAnim("attack", "rest_out");

        if (animatable.getPlayer().getData(DataAttachments.PAIRING_STATE) >= 1 && animatable.getPlayer().getData(DataAttachments.PAIRING_STATE) < 3) animatable.triggerAnim("attack", "courting_male");

        if (animationState.isCurrentAnimation(Deinonychus.WALK_ANIM)) {
            animationState.setControllerSpeed(1.3f);
        } else if (animationState.isCurrentAnimation(Deinonychus.RUN_ANIM)) {
            animationState.setControllerSpeed(0.15f);
        } else {
            animationState.setControllerSpeed(1f);
        }


        float partialTick = animationState.getPartialTick();


        handleBodyRotation(this, animationState.getAnimatable(), partialTick);
        handleNeckRotation(this, animationState.getAnimatable(), partialTick);
        handleTailPhysics(this, animationState.getAnimatable(), partialTick);


    }

    private void handleBodyRotation(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {
        float playerRot = animatable.getPlayer().getData(DataAttachments.PLAYER_TURN);
        float rotProgress = animatable.getPlayer().getData(DataAttachments.TURN_PROGRESS);

        GeoBone center = getAnimationProcessor().getBone("center");
        GeoBone leaningHandler = getAnimationProcessor().getBone("leaning_handler");


        if (animatable.playerId == null) {
            return;
        }

        float currentRotY = Mth.lerp(partialTick, animatable.prevRotY, playerRot);

        center.setRotY(currentRotY);
        leaningHandler.setRotZ(rotProgress * 0.2f);

        animatable.prevRotY = currentRotY;
    }




    private void handleNeckRotation(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {

        GeoBone head_tilt = getAnimationProcessor().getBone("head_tilt");
        GeoBone neck_tilt = getAnimationProcessor().getBone("neck_tilt");


        if (animatable.playerId == null) {
            return;
        }

        float playerRot = (animatable.getPlayer().getData(DataAttachments.PLAYER_TURN));
        float playerYaw = (animatable.getPlayer().getYRot() * -Mth.DEG_TO_RAD);

        float playerDiff = playerYaw - playerRot;

        while (playerDiff > 3.14159f) {
            playerDiff = playerDiff - 6.28319f;
        }

        while (playerDiff < -3.14159f) {
            playerDiff = playerDiff + 6.28319f;
        }

        if (playerDiff < 0) {
            playerDiff = Math.max(-0.5f, playerDiff);
        } else {
            playerDiff = Math.min(0.5f, playerDiff);
        }

        float headRot = playerDiff * 0.8f;
        float neckRot = playerDiff * 0.8f;


        neck_tilt.setRotY(neckRot);
        head_tilt.setRotY(headRot);

    }

    private void handleTailPhysics(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {

        GeoBone tail_1_rot = getAnimationProcessor().getBone("tail_1_rot");
        GeoBone tail_2_rot = getAnimationProcessor().getBone("tail_2_rot");
        GeoBone tail_3_rot = getAnimationProcessor().getBone("tail_3_rot");

        ArrayList<Float> yHistory = animatable.getPlayer().getData(DataAttachments.Y_HISTORY);
        ArrayList<Float> turnDegreeHistory = animatable.getPlayer().getData(DataAttachments.TURN_HISTORY);

        if (yHistory.size() < 6 || turnDegreeHistory.size() < 9) return;

        float tailRotX = 35 * Util.calculateTailXRot(yHistory);
        float tailRotY = -30 * Util.calculateTailYRot(turnDegreeHistory,
                animatable.getPlayer().getData(DataAttachments.PLAYER_TURN));

        float currentRotY = Mth.lerp(partialTick, animatable.prevTailRotY, tailRotY);
        float currentRotX = Mth.lerp(partialTick, animatable.prevTailRotX, tailRotX);

        tail_1_rot.setRotX(currentRotX);
        tail_2_rot.setRotX(currentRotX);
        tail_3_rot.setRotX(currentRotX);

        tail_1_rot.setRotY(currentRotY);
        tail_2_rot.setRotY(currentRotY);
        tail_3_rot.setRotY(currentRotY);

        animatable.prevTailRotX = currentRotX;
        animatable.prevTailRotY = currentRotY;
    }
}
