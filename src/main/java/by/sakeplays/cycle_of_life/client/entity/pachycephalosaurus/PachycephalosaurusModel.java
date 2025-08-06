package by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;

public class PachycephalosaurusModel extends GeoModel<Pachycephalosaurus> {
    @Override
    public ResourceLocation getModelResource(Pachycephalosaurus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/pachycephalosaurus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Pachycephalosaurus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/entity/pachycephalosaurus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Pachycephalosaurus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "animations/pachycephalosaurus.animation.json");
    }

    @Override
    public void setCustomAnimations(Pachycephalosaurus animatable, long instanceId, AnimationState<Pachycephalosaurus> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);


        if (!animatable.isBody()) {

            if (animatable.getPlayer().getData(DataAttachments.ATTACK_MAIN_1)) animatable.triggerAnim("attack", "bash");
            if (animatable.getPlayer().getData(DataAttachments.ATTACK_TURNAROUND)) animatable.triggerAnim("attack", "upper_bash");

            if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 1)
                animatable.triggerAnim("attack", "rest_in");
            if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 2)
                animatable.triggerAnim("attack", "rest_loop");
            if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 3)
                animatable.triggerAnim("attack", "rest_out");

        }

        float partialTick = animationState.getPartialTick();

        handleBodyRotation(this, animatable, partialTick);
        handleNeckRotation(this, animatable, partialTick);
        handleTailPhysics(this, animatable, partialTick);

    }

    private void handleBodyRotation(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {

        float playerRot = 0;

        GeoBone center = getAnimationProcessor().getBone("root");


        if (animatable.playerId != null && !animatable.isBody()) {
            playerRot = -animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
        }

        float currentRotY = Mth.lerp(partialTick, animatable.prevRotY, playerRot);

        center.setRotY(currentRotY);

        animatable.prevRotY = currentRotY;
    }


    private void handleNeckRotation(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {

        GeoBone head_tilt = getAnimationProcessor().getBone("head_rot");
        GeoBone neck_tilt = getAnimationProcessor().getBone("neck_rot");

        float playerRot = 0;
        float playerYaw = 0;
        float additionalTurn = 0;
        float targetYaw = 0;
        float rot = animatable.headRot;

        if (animatable.playerId != null && !animatable.isBody()) {
            playerRot =animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
            playerYaw = animatable.getPlayer().getYRot();
            additionalTurn = animatable.getPlayer().getData(DataAttachments.ADDITIONAL_TURN);
            targetYaw = playerYaw * Mth.DEG_TO_RAD + additionalTurn;
        }



        float desiredHeadRot = -Math.clamp(
                Mth.wrapDegrees((float) Math.toDegrees((targetYaw - playerRot) - additionalTurn)) * Mth.DEG_TO_RAD,
                -0.35f, 0.35f);

        rot += (desiredHeadRot - rot) * 0.13f / ((float) 60 / Math.min(60, Minecraft.getInstance().getFps() + 1));


        neck_tilt.setRotY(rot * 1.15f);
        head_tilt.setRotY(rot * 1.5f);


        animatable.headRot = rot;

    }


    private void handleTailPhysics(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {

        GeoBone tail_1_rot = getAnimationProcessor().getBone("tail_1_rot");
        GeoBone tail_2_rot = getAnimationProcessor().getBone("tail_2_rot");
        GeoBone tail_3_rot = getAnimationProcessor().getBone("tail_3_rot");
        GeoBone leaningHandler = getAnimationProcessor().getBone("leaning_handler");

        float currentRotY1 = 0;
        float currentRotY2 = 0;
        float currentRotY3 = 0;

        float currentRotX = 0;
        float speed = 1;

        if (animatable.playerId != null && !animatable.isBody()) {

            speed += animatable.getPlayer().getData(DataAttachments.SPEED);

            ArrayList<Float> yHistory = animatable.getPlayer().getData(DataAttachments.Y_HISTORY);
            ArrayList<Float> turnDegreeHistory = animatable.getPlayer().getData(DataAttachments.TURN_HISTORY);

            if (yHistory.size() < 6 || turnDegreeHistory.size() < 7) return;

            float tailRotX = 35 * Util.calculateTailXRot(yHistory);
            float tailRotY1 = (20 * Util.calculateTailYRot(turnDegreeHistory,
                    animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION), 0, 2)) / speed;

            float tailRotY2 = (30 * Util.calculateTailYRot(turnDegreeHistory,
                    animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION), 2, 4)) / speed;
            float tailRotY3 = (40 * Util.calculateTailYRot(turnDegreeHistory,
                    animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION), 4, 6)) / speed;

            currentRotY1 = Mth.lerp(partialTick, animatable.prevTailRotY1, tailRotY1);
            currentRotY2 = Mth.lerp(partialTick, animatable.prevTailRotY2, tailRotY2);
            currentRotY3 = Mth.lerp(partialTick, animatable.prevTailRotY3, tailRotY3);

            currentRotX = Mth.lerp(partialTick, animatable.prevTailRotX, tailRotX);

        }

        leaningHandler.setRotZ(currentRotY1 / -1.75f);

        tail_1_rot.setRotX(currentRotX);
        tail_2_rot.setRotX(currentRotX);
        tail_3_rot.setRotX(currentRotX);

        tail_1_rot.setRotY(currentRotY1);
        tail_2_rot.setRotY(currentRotY2);
        tail_3_rot.setRotY(currentRotY3);

        animatable.prevTailRotX = currentRotX;
        animatable.prevTailRotY1 = currentRotY1;
        animatable.prevTailRotY2 = currentRotY2;
        animatable.prevTailRotY3 = currentRotY3;

    }
}
