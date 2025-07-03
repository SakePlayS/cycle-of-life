package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;

public class DeinonychusModel extends GeoModel<Deinonychus> {


    @Override
    public ResourceLocation getModelResource(Deinonychus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/deinonychus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Deinonychus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/entity/deinonychus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Deinonychus animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "animations/deinonychus.animation.json");
    }

    @Override
    public void setCustomAnimations(Deinonychus animatable, long instanceId, AnimationState<Deinonychus> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);


        if (animationState.isCurrentAnimation(Deinonychus.WALK_ANIM)) {
            animationState.setControllerSpeed(1.3f);
        } else {
            animationState.setControllerSpeed(1f);
        }

        float partialTick = animationState.getPartialTick();

        handleBodyRotation(this, animationState.getAnimatable(), partialTick);
        handleNeckRotation(this, animationState.getAnimatable(), partialTick);
        handleTailPhysics(this, animationState.getAnimatable(), partialTick);

        animatable.getPlayer().sendSystemMessage(Component.literal(animatable.getPlayer().getName().getString() + " " + instanceId));

    }

    private void handleBodyRotation(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {
        float playerRot = animatable.getPlayer().getData(DataAttachments.PLAYER_TURN);

        if (animatable.playerId == null) {
            return;
        }
        model.getBone("center").get().setRotY(Mth.lerp(partialTick, model.getBone("center").get().getRotY(), playerRot));

    }




    private void handleNeckRotation(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {

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

        float headRot = (Mth.lerp(partialTick, model.getBone("head_tilt").get().getRotY(), playerDiff * 0.8f));
        float neckRot = Mth.lerp(partialTick, model.getBone("neck_tilt").get().getRotY(), playerDiff * 0.8f);

        model.getBone("head_tilt").get().setRotY(headRot);
        model.getBone("neck_tilt").get().setRotY(neckRot);

    }

    private void handleTailPhysics(GeoModel<Deinonychus> model, Deinonychus animatable, float partialTick) {


        ArrayList<Float> yHistory = animatable.getPlayer().getData(DataAttachments.Y_HISTORY);
        ArrayList<Float> turnDegreeHistory = animatable.getPlayer().getData(DataAttachments.TURN_HISTORY);

        if (yHistory.size() < 6) {
            return;
        }

        if (turnDegreeHistory.size() < 9) {
            return;
        }

        float tailRotX = Util.calculateTailXRot(yHistory);
        float tailRotY = Util.calculateTailYRot(turnDegreeHistory,
                animatable.getPlayer().getData(DataAttachments.PLAYER_TURN));

        model.getBone("tail_1_rot").get()
                .setRotX(Mth.lerp(partialTick, model.getBone("tail_1_rot").get().getRotX(),tailRotX * 35));
        model.getBone("tail_2_rot").get()
                .setRotX(Mth.lerp(partialTick, model.getBone("tail_2_rot").get().getRotX(),tailRotX * 35));
        model.getBone("tail_3_rot").get()
                .setRotX(Mth.lerp(partialTick, model.getBone("tail_3_rot").get().getRotX(),tailRotX * 35));


        model.getBone("tail_1_rot").get()
                .setRotY(Mth.lerp(partialTick, model.getBone("tail_1_rot").get().getRotY(),tailRotY * -35));
        model.getBone("tail_2_rot").get()
                .setRotY(Mth.lerp(partialTick, model.getBone("tail_2_rot").get().getRotY(),tailRotY * -35));
        model.getBone("tail_3_rot").get()
                .setRotY(Mth.lerp(partialTick, model.getBone("tail_3_rot").get().getRotY(),tailRotY * -35));
    }
}
