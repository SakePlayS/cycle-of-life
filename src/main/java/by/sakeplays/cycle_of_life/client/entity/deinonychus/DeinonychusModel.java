package by.sakeplays.cycle_of_life.client.entity.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

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


        if (!animatable.isCorpse()) {

            if (animatable.getPlayer().getData(DataAttachments.ATTACK_MAIN_1)) {
                animatable.triggerAnim("attack", "bite");
            }

            if (animatable.getPlayer().getData(DataAttachments.ATTACK_MAIN_2)) {
                if (Math.random() < 0.5) {
                    animatable.triggerAnim("attack", "slash_right");
                } else {
                    animatable.triggerAnim("attack", "slash_left");
                }
            }

            if (animatable.getPlayer().getData(DataAttachments.ALT_ATTACK))
                animatable.triggerAnim("attack", "turnaround_slash");
            if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 1)
                animatable.triggerAnim("attack", "rest_in");
            if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 2)
                animatable.triggerAnim("attack", "rest_loop");
            if (animatable.getPlayer().getData(DataAttachments.RESTING_STATE) == 3)
                animatable.triggerAnim("attack", "rest_out");

            if (animatable.getPlayer().getData(DataAttachments.PAIRING_STATE) >= 1 && animatable.getPlayer().getData(DataAttachments.PAIRING_STATE) < 3) {
                if (animatable.getPlayer().getData(DataAttachments.DINO_DATA).isMale()) {
                    animatable.triggerAnim("attack", "courting_male");
                } else {
                    animatable.triggerAnim("attack", "courting_female");
                }
            }

            if (animationState.isCurrentAnimation(Deinonychus.WALK_ANIM)) {
                animationState.setControllerSpeed(1.3f);
            } else if (animationState.isCurrentAnimation(Deinonychus.RUN_ANIM)) {
                animationState.setControllerSpeed(0.15f);
            } else {
                animationState.setControllerSpeed(1f);
            }

            if (animatable.getPlayer().tickCount % 20 == 0) {
                if (Math.random() < 0.1) animatable.triggerAnim("blink", "blink");
            }
        }


        float partialTick = animationState.getPartialTick();


        handleBodyRotation(this, animationState.getAnimatable(), partialTick);
        handleNeckRotation(this, animationState.getAnimatable(), partialTick);
        handleTailPhysics(animatable, partialTick);

    }

    private void handleBodyRotation(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {

        float playerRot = 0;

        GeoBone center = getAnimationProcessor().getBone("center");


        if (animatable.playerId != null && !animatable.isCorpse()) {
            playerRot = -animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
        }

        float currentRotY = Mth.lerp(partialTick, animatable.prevRotY, playerRot);

        center.setRotY(currentRotY);

        animatable.prevRotY = currentRotY;
    }




    private void handleNeckRotation(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {

        GeoBone head_tilt = getAnimationProcessor().getBone("head_tilt");
        GeoBone neck_tilt = getAnimationProcessor().getBone("neck_tilt");

        float playerRot = 0;
        float playerYaw = 0;
        float additionalTurn = 0;
        float targetYaw = 0;
        float rot = animatable.headRot;

        if (animatable.isCorpse() || animatable.getPlayer() == null) {
            neck_tilt.setRotX(0);
            neck_tilt.setRotY(0);
            return;
        }

        if (animatable.playerId != null && !animatable.isCorpse()) {
            playerRot =animatable.getPlayer().getData(DataAttachments.PLAYER_ROTATION);
            playerYaw = animatable.getPlayer().getYRot();
            additionalTurn = animatable.getPlayer().getData(DataAttachments.ADDITIONAL_TURN);
            targetYaw = playerYaw * Mth.DEG_TO_RAD + additionalTurn;
        }



        float desiredHeadRotY = -Math.clamp(
                Mth.wrapDegrees((float) Math.toDegrees((targetYaw - playerRot) - additionalTurn)) * Mth.DEG_TO_RAD,
                -0.35f, 0.35f);

        rot += (desiredHeadRotY - rot) * 0.13f / ((float) 60 / Math.min(60, Minecraft.getInstance().getFps() + 1));

        neck_tilt.setRotY(rot * 1.15f);
        head_tilt.setRotY(rot * 1.5f);

        neck_tilt.setRotX(Math.clamp(animatable.getPlayer().getXRot() - 20, -70, 70) * Mth.DEG_TO_RAD * -0.2f);
        head_tilt.setRotX(Math.clamp(animatable.getPlayer().getXRot() - 20, -70, 70) * Mth.DEG_TO_RAD * -0.8f);

        animatable.headRot = rot;

    }

    private void handleTailPhysics(Deinonychus animatable, float partialTick) {

        GeoBone tail_1_rot = getAnimationProcessor().getBone("tail_1_rot");
        GeoBone tail_2_rot = getAnimationProcessor().getBone("tail_2_rot");
        GeoBone tail_3_rot = getAnimationProcessor().getBone("tail_3_rot");
        GeoBone tail_root = getAnimationProcessor().getBone("tail_root");

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

            animatable.recordTailPosHistory(6, animatable.tailRootPos);

            if (animatable.tailPosHistory.size() < 6) {
                tail_1_rot.setRotY(0);
                tail_1_rot.setRotX(0);

                tail_2_rot.setRotY(0);
                tail_2_rot.setRotX(0);

                tail_3_rot.setRotY(0);
                tail_3_rot.setRotX(0);
                return;
            }

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
                animatable.tailRotY += (targetYaw - animatable.tailRotY) * 0.6F;
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

        if (animatable.tailRotXHistory.size() < 15 || animatable.tailRotYHistory.size() < 15) {
            tail_1_rot.setRotY(0);
            tail_1_rot.setRotX(0);

            tail_2_rot.setRotY(0);
            tail_2_rot.setRotX(0);

            tail_3_rot.setRotY(0);
            tail_3_rot.setRotX(0);
            return;
        }

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
