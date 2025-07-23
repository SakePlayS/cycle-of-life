package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

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

            if (animatable.getPlayer().getData(DataAttachments.ATTACK_MAIN_1)) {
                animatable.triggerAnim("attack", "bash");
            }
        }

        float partialTick = animationState.getPartialTick();

        handleBodyRotation(this, animatable, partialTick);
        handleNeckRotation(this, animatable, partialTick);

    }

    private void handleBodyRotation(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {

        float playerRot = 0;

        GeoBone center = getAnimationProcessor().getBone("root");


        if (animatable.playerId != null && !animatable.isBody()) {
            playerRot = -animatable.getPlayer().getData(DataAttachments.PLAYER_TURN);
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
            playerRot =animatable.getPlayer().getData(DataAttachments.PLAYER_TURN);
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
}
