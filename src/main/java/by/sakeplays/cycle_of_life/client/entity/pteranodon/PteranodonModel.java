package by.sakeplays.cycle_of_life.client.entity.pteranodon;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Pteranodon;
import by.sakeplays.cycle_of_life.event.client.CameraEvent;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;

public class PteranodonModel extends GeoModel<Pteranodon> {


    @Override
    public ResourceLocation getModelResource(Pteranodon animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/pteranodon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Pteranodon animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/entity/pteranodon/pteranodon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Pteranodon animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "animations/pteranodon.animation.json");
    }

    @Override
    public void setCustomAnimations(Pteranodon animatable, long instanceId, AnimationState<Pteranodon> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animatable.isForScreenRendering) return;

        float partialTick = animationState.getPartialTick();

        if (animatable.getPlayer() != null && animatable.lastUpdatedTick != animatable.getPlayer().tickCount) {

            animatable.clientRenderTick();
            animatable.lastUpdatedTick = animatable.getPlayer().tickCount;
        }

        handleBodyRotation(this, animationState.getAnimatable(), partialTick);
        handleNeckRotation(this, animationState.getAnimatable(), partialTick);
        handleFlightRotation(this, animationState.getAnimatable(), partialTick);

    }

    private void handleBodyRotation(GeoModel<Pteranodon> model, Pteranodon animatable, float partialTick) {

        float playerRot = 0;

        GeoBone center = getAnimationProcessor().getBone("root");


        if (animatable.playerId != null && !animatable.isCorpse()) {
            playerRot = -animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
        }

        float currentRotY = Mth.lerp(partialTick, animatable.prevRotY, playerRot);

        center.setRotY(currentRotY);

        animatable.prevRotY = currentRotY;
    }

    private void handleNeckRotation(GeoModel<Pteranodon> model, Pteranodon animatable, float partialTick) {


        GeoBone head_tilt = getAnimationProcessor().getBone("head_tilt");
        GeoBone neck_tilt = getAnimationProcessor().getBone("neck_tilt");

        float playerRot = 0;
        float playerYaw = 0;
        float additionalTurn = 0;
        float targetYaw = 0;
        float rot = animatable.headRot;

        if (animatable.playerId != null && !animatable.isCorpse()) {
            playerRot =animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
            playerYaw = animatable.getPlayer().getYRot();
            additionalTurn = animatable.getPlayer().getData(DataAttachments.ADDITIONAL_TURN);
            targetYaw = playerYaw * Mth.DEG_TO_RAD + additionalTurn;
        }

        float flightFactor = !animatable.getPlayer().getData(DataAttachments.DINO_DATA).isFlying() ? 1 : 0;

        float desiredHeadRot = -Math.clamp(
                Mth.wrapDegrees((float) Math.toDegrees((targetYaw - playerRot) - additionalTurn)) * Mth.DEG_TO_RAD,
                -0.35f, 0.35f);

        rot += flightFactor * ((desiredHeadRot - rot) * 0.13f / ((float) 60 / Math.min(60, Minecraft.getInstance().getFps() + 1)));


        neck_tilt.setRotY(rot * 1.15f);
        head_tilt.setRotY(rot * 1.5f);

        animatable.headRot = rot;

    }

    // TODO: add interpolation
    private void handleFlightRotation(GeoModel<Pteranodon> model, Pteranodon animatable, float partialTick) {

        GeoBone root = getAnimationProcessor().getBone("root");

        Player player = animatable.getPlayer();

        if (player == null) return;
        float flightFactor = animatable.getPlayer().getData(DataAttachments.DINO_DATA).isFlying() ? 1 : 0;

        float delta = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();

        animatable.airbrakeFactor =
                player.getData(DataAttachments.DINO_DATA).isAirbraking() ?
                Math.max(0, animatable.airbrakeFactor - 0.075f * delta) :
                Math.min(1, animatable.airbrakeFactor + 0.075f * delta);


        float xzDelta = new Vec2((float)(player.getX() - player.xOld), (float)(player.getZ() - player.zOld)).length();
        float yDelta = (float) (player.getY() - player.yOld);

        float xRot = (float) Math.atan2(yDelta, xzDelta) * flightFactor;

        root.setRotX(xRot * animatable.airbrakeFactor);

        ArrayList<Float> turnDegreeHistory = animatable.getPlayer().getData(DataAttachments.TURN_HISTORY);


        if (turnDegreeHistory.size() >= 7) {
            GeoBone flightHelper = getAnimationProcessor().getBone("flight_helper");
            GeoBone head_tilt = getAnimationProcessor().getBone("head_tilt");
            GeoBone neck_tilt = getAnimationProcessor().getBone("neck_tilt");

            float zRot = (turnDegreeHistory.getLast() - turnDegreeHistory.get(turnDegreeHistory.size() - 2)) * flightFactor;

            flightHelper.setRotZ(zRot * -7 * animatable.airbrakeFactor);

            neck_tilt.setRotZ(zRot * 3);
            neck_tilt.setRotY(zRot * -2.5f);
            neck_tilt.setRotX(Math.abs(zRot));

            head_tilt.setRotZ(zRot * 3);
            head_tilt.setRotY(zRot * -2.5f);
            neck_tilt.setRotX(Math.abs(zRot));

        }

        if (animatable.yMomentumHistory.size() > 10) {
            GeoBone rightLegPhysics = getAnimationProcessor().getBone("right_leg_physics_handler");
            GeoBone leftLegPhysics = getAnimationProcessor().getBone("left_leg_physics_handler");
            GeoBone rightKneePhysics = getAnimationProcessor().getBone("right_knee_physics_handler");
            GeoBone leftKneePhysics = getAnimationProcessor().getBone("left_knee_physics_handler");
            GeoBone neckPhysics = getAnimationProcessor().getBone("neck_physics_handler");
            GeoBone headPhysics = getAnimationProcessor().getBone("head_physics_handler");

            float seg1 = (Util.getFromEnd(animatable.yMomentumHistory, 0) - Util.getFromEnd(animatable.yMomentumHistory, 1));
            float seg2 = (Util.getFromEnd(animatable.yMomentumHistory, 2) - Util.getFromEnd(animatable.yMomentumHistory, 3));
            float deltaTime = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();

            animatable.targetLegRotX1 = seg1;
            animatable.targetLegRotX2 = seg2;

            animatable.legRotX1 = animatable.legRotX1 + (animatable.targetLegRotX1 - animatable.legRotX1) * 0.3f * deltaTime;
            animatable.legRotX2 = animatable.legRotX2 + (animatable.targetLegRotX2 - animatable.legRotX2) * 0.3f * deltaTime;

            rightLegPhysics.setRotX(-animatable.legRotX1 * 3f);
            leftLegPhysics.setRotX(-animatable.legRotX1 * 3f);

            rightKneePhysics.setRotX(-animatable.legRotX2 * 3f);
            leftKneePhysics.setRotX(-animatable.legRotX2 * 3f);

            neckPhysics.setRotX(animatable.legRotX1 * 2);
            headPhysics.setRotX(animatable.legRotX2 * 2);

        }
    }




}
