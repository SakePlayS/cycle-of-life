package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.common.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class DeinonychusRenderer extends GeoEntityRenderer<Deinonychus>  {
    public DeinonychusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeinonychusModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Deinonychus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.scale(0.9f, 0.9f, 0.9f);

        handleNeckRotation(model, animatable, partialTick);
        handleBodyRotation(model, animatable, partialTick);

    }

    private void handleBodyRotation(BakedGeoModel model, Deinonychus animatable, float partialTick) {
        float playerRot = animatable.getPlayer().getData(DataAttachments.TURN_DEGREE);

        if (animatable.playerId == null) {
            return;
        }
        model.getBone("center").get().setRotY(Mth.lerp(partialTick, model.getBone("center").get().getRotY(), playerRot));
    }

    private void handleNeckRotation(BakedGeoModel model, Deinonychus animatable, float partialTick) {
        float playerRot = (animatable.getPlayer().getData(DataAttachments.TURN_DEGREE));
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

        if (animatable.playerId == null) {
            return;
        }

        model.getBone("head_tilt").get().setRotY(Mth.lerp(partialTick, model.getBone("head_tilt").get().getRotY(), playerDiff * 0.8f));
        model.getBone("neck_tilt").get().setRotY(Mth.lerp(partialTick, model.getBone("neck_tilt").get().getRotY(), playerDiff * 0.8f));
    }

    @Override
    public long getInstanceId(Deinonychus animatable) {
        return animatable.getPlayer().getId();
    }


}
