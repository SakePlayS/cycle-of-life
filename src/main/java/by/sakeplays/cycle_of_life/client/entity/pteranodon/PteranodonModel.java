package by.sakeplays.cycle_of_life.client.entity.pteranodon;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Pteranodon;
import by.sakeplays.cycle_of_life.event.client.CameraEvent;
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

        if (animatable.lastUpdatedTick != animatable.getPlayer().tickCount) {
            animatable.lastUpdatedTick = animatable.getPlayer().tickCount;

            animatable.recordRotHistory(animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION), 2);
        }

        handleBodyRotation(this, animationState.getAnimatable(), partialTick);

        if (animatable.getPlayer().getData(DataAttachments.DINO_DATA).getFlightState() != 2) {
            handleNeckRotation(this, animationState.getAnimatable(), partialTick);
        } else {
            handleFlightRotation(this, animationState.getAnimatable(), partialTick);
        }

    }

    private void handleBodyRotation(GeoModel<Pteranodon> model, Pteranodon animatable, float partialTick) {

        float playerRot = 0;

        GeoBone center = getAnimationProcessor().getBone("root");


        if (animatable.playerId != null && !animatable.isBody()) {
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

    // TODO: add interpolation
    private void handleFlightRotation(GeoModel<Pteranodon> model, Pteranodon animatable, float partialTick) {

        GeoBone root = getAnimationProcessor().getBone("root");

        Player player = animatable.getPlayer();

        if (player == null) return;

        float airbrakeFactor = player.getData(DataAttachments.DINO_DATA).isAirbraking() ? 0 : 1;

        float xzDelta = new Vec2((float)(player.getX() - player.xOld), (float)(player.getZ() - player.zOld)).length();
        float yDelta = (float) (player.getY() - player.yOld);

        float xRot = (float) Math.atan2(yDelta, xzDelta);

        if (player.getId() == Minecraft.getInstance().player.getId()) {
            CameraEvent.rawPitch = xRot * -Mth.RAD_TO_DEG;
        } else {
            CameraEvent.rawPitch = 0;
        }

        root.setRotX(xRot * airbrakeFactor);

        ArrayList<Float> turnDegreeHistory = animatable.getPlayer().getData(DataAttachments.TURN_HISTORY);


        if (turnDegreeHistory.size() >= 7) {
            GeoBone flightHelper = getAnimationProcessor().getBone("flight_helper");
            GeoBone head_tilt = getAnimationProcessor().getBone("head_tilt");
            GeoBone neck_tilt = getAnimationProcessor().getBone("neck_tilt");

            float zRot = turnDegreeHistory.getLast() - turnDegreeHistory.get(turnDegreeHistory.size() - 2);

            if (player.getId() == Minecraft.getInstance().player.getId()) {
                CameraEvent.rawRoll = zRot * 5 * Mth.RAD_TO_DEG;
            } else {
                CameraEvent.rawRoll = 0;
            }

            flightHelper.setRotZ(zRot * -7 * airbrakeFactor);

            neck_tilt.setRotZ(zRot * 3 * airbrakeFactor);
            neck_tilt.setRotY(zRot * -2.5f * airbrakeFactor);
            neck_tilt.setRotX(Math.abs(zRot) * airbrakeFactor);

            head_tilt.setRotZ(zRot * 3 * airbrakeFactor);
            head_tilt.setRotY(zRot * -2.5f * airbrakeFactor);
            neck_tilt.setRotX(Math.abs(zRot) * airbrakeFactor);

        }
    }




}
