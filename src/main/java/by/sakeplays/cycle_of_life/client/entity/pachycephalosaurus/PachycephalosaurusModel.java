package by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
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


        if (!animatable.isCorpse()) {

            // TODO: migrate this to a controller

            if (animatable.getPlayer().getData(DataAttachments.ATTACK_MAIN_1)) animatable.triggerAnim("attack", "bash");
            if (animatable.getPlayer().getData(DataAttachments.ALT_ATTACK)) animatable.triggerAnim("attack", "upper_bash");

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
        handleTailPhysics(animatable, partialTick);

    }

    private void handleBodyRotation(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {

        float playerRot = 0;

        GeoBone center = getAnimationProcessor().getBone("root");


        if (animatable.playerId != null && !animatable.isCorpse()) {
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

        if (animatable.playerId != null && !animatable.isCorpse()) {
            playerRot =animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
            playerYaw = animatable.getPlayer().getYRot();
            additionalTurn = animatable.getPlayer().getData(DataAttachments.ADDITIONAL_TURN);
            targetYaw = playerYaw * Mth.DEG_TO_RAD + additionalTurn;
        }



        float desiredHeadRot = -Math.clamp(
                Mth.wrapDegrees((float) Math.toDegrees((targetYaw - playerRot) - additionalTurn)) * Mth.DEG_TO_RAD,
                -0.35f, 0.35f);

        rot += (desiredHeadRot - rot) * 0.13f * Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();


        neck_tilt.setRotY(rot * 1.15f);
        head_tilt.setRotY(rot * 1.5f);

        animatable.headRot = rot;
    }


    private void handleTailPhysics(Pachycephalosaurus animatable, float partialTick) {

        GeoBone tail_1_rot = getAnimationProcessor().getBone("tail_1_rot");
        GeoBone tail_2_rot = getAnimationProcessor().getBone("tail_2_rot");
        GeoBone tail_3_rot = getAnimationProcessor().getBone("tail_3_rot");

        if (animatable.getPlayer() == null || animatable.isForScreenRendering || animatable.isCorpse()) {
            tail_1_rot.setRotY(0);
            tail_1_rot.setRotX(0);

            tail_2_rot.setRotY(0);
            tail_2_rot.setRotX(0);

            tail_3_rot.setRotY(0);
            tail_3_rot.setRotX(0);
            return;
        }

        if (animatable.lastUpdatedTick != animatable.getPlayer().tickCount) {
            animatable.lastUpdatedTick = animatable.getPlayer().tickCount;

            GeoBone tail_root = getAnimationProcessor().getBone("tail_root");

            animatable.recordTailPosHistory(6, animatable.tailRootPos);

            if (animatable.tailPosHistory.size() < 6) return;


            float dxz = new Vec2(
                    (float) (Util.getFromEnd(animatable.tailPosHistory, 0).x() - Util.getFromEnd(animatable.tailPosHistory, 1).x()),
                    (float) (Util.getFromEnd(animatable.tailPosHistory, 0).z() - Util.getFromEnd(animatable.tailPosHistory, 1).z())).length() + 0.2f;
            float dy = (float) ((Util.getFromEnd(animatable.tailPosHistory, 0).y() - Util.getFromEnd(animatable.tailPosHistory, 1).y()));


            float desiredTailRotX = (float) Math.atan2(dy, dxz);
            animatable.tailRotX = animatable.tailRotX + ((desiredTailRotX - animatable.tailRotX) * 0.8f);
            animatable.recordTailRotXHistory(15);

            Position currPos = Util.getFromEnd(animatable.tailPosHistory, 0);
            Position prevPos = Util.getFromEnd(animatable.tailPosHistory, 2);

            float f = (float) Math.atan2(currPos.z() - prevPos.z(), currPos.x() - prevPos.x());

            if (new Vec2((float) (currPos.z() - prevPos.z()), (float) (currPos.x() - prevPos.x())).length() > 0.1f) {
                float playerRot = animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
                float targetYaw = (float) Math.atan2(Math.sin(f - playerRot), Math.cos(f - playerRot)) - 1.570796f;
                targetYaw = (float) Math.atan2(Math.sin(targetYaw), Math.cos(targetYaw));
                animatable.tailRotY += (targetYaw - animatable.tailRotY) * 0.4F;
                animatable.tailRotY *= 0.8F;

                float bodyYawChange = animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION) - animatable.prevBodyRot;
                bodyYawChange = (float)Math.atan2(Math.sin(bodyYawChange), Math.cos(bodyYawChange));
                animatable.prevBodyRot = animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);

                animatable.tailRotY -= bodyYawChange * 0.8F;

            }
            animatable.recordTailRotYHistory(15);

            animatable.oldTailRootPos = new Position(
                    animatable.getPlayer().getPosition(partialTick).x + (tail_root.getWorldPosition()).x,
                    animatable.getPlayer().getPosition(partialTick).y + (tail_root.getWorldPosition()).y,
                    animatable.getPlayer().getPosition(partialTick).z + (tail_root.getWorldPosition()).z);

        }

        if (animatable.tailRotXHistory.size() < 15 || animatable.tailRotYHistory.size() < 15) return;

        tail_1_rot.setRotX(0.5f * Mth.lerp
                (
                        partialTick,
                        Util.getFromEnd(animatable.tailRotXHistory, 1),
                        Util.getFromEnd(animatable.tailRotXHistory, 0)
                )
        );

        tail_2_rot.setRotX(0.7f * Mth.lerp
                (
                        partialTick,
                        Util.getFromEnd(animatable.tailRotXHistory, 3),
                        Util.getFromEnd(animatable.tailRotXHistory, 2)
                )
        );

        tail_3_rot.setRotX( Mth.lerp
                (
                        partialTick,
                        Util.getFromEnd(animatable.tailRotXHistory, 5),
                        Util.getFromEnd(animatable.tailRotXHistory, 4)
                )
        );



        tail_1_rot.setRotY(0.4f * -Mth.lerp
                (
                        partialTick,
                        Util.getFromEnd(animatable.tailRotYHistory, 1),
                        Util.getFromEnd(animatable.tailRotYHistory, 0)
                )
        );

        tail_2_rot.setRotY(0.6f * -Mth.lerp
                (
                        partialTick,
                        Util.getFromEnd(animatable.tailRotYHistory, 3),
                        Util.getFromEnd(animatable.tailRotYHistory, 2)
                )
        );

        tail_3_rot.setRotY(0.8f * -Mth.lerp
                (
                        partialTick,
                        Util.getFromEnd(animatable.tailRotYHistory, 5),
                        Util.getFromEnd(animatable.tailRotYHistory, 4)
                )
        );
    }
}
