package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
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

        float partialTick = animationState.getPartialTick();

        handleBodyRotation(this, animatable, partialTick);
        handleNeckRotation(this, animatable, partialTick);

    }

    private void handleBodyRotation(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {
        float playerRot = animatable.getPlayer().getData(DataAttachments.DINO_DATA).getTurnDegree();

        model.getBone("root").get().setRotY(Mth.lerp(partialTick, model.getBone("root").get().getRotY(), playerRot));
    }



    private void handleNeckRotation(GeoModel<Pachycephalosaurus> model, Pachycephalosaurus animatable, float partialTick) {
        float playerRot = (animatable.getPlayer().getData(DataAttachments.DINO_DATA).getTurnDegree());
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

        model.getBone("head_rot").get().setRotY(Mth.lerp(partialTick, model.getBone("head_rot").get().getRotY(), playerDiff * 0.8f));
        model.getBone("neck_rot").get().setRotY(Mth.lerp(partialTick, model.getBone("neck_rot").get().getRotY(), playerDiff * 0.8f));
    }
}
