package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PachycephalosaurusRenderer extends GeoEntityRenderer<Pachycephalosaurus> {
    public PachycephalosaurusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PachycephalosaurusModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);


        if (animatable.playerId == null) {
            return;
        }

        handleBodyRotation(model, animatable);
        //handleHeadRotation(model, animatable);

    }


    private void handleBodyRotation(BakedGeoModel model, Pachycephalosaurus animatable) {
        float playerRot = animatable.getPlayer().getYRot();
        float boneRot =  model.getBone("root").get().getRotY() * Mth.RAD_TO_DEG;
        float boneRotRad = model.getBone("root").get().getRotY();
        float difference = playerRot + boneRot;


        model.getBone("root").get().setRotY(boneRotRad + -Mth.DEG_TO_RAD * difference/7);
    }

    private void handleHeadRotation(BakedGeoModel model, Pachycephalosaurus animatable) {
        float playerRot = animatable.getPlayer().getYRot();
        float headRot =  model.getBone("head_rot").get().getRotY() * Mth.RAD_TO_DEG;
        float headRotRad = model.getBone("head_rot").get().getRotY();
        float neckRotRad = model.getBone("neck_rot").get().getRotY();
        float difference = playerRot + headRot;

        float rot = headRotRad + -Mth.DEG_TO_RAD * difference / 7;
        rot = rot * 0.85f * Math.min(difference, 1);

        if (rot > 0.4f) {
            rot = 0.4f;
        } else if (rot < -0.4f) {
            rot = -0.4f;
        }

        model.getBone("head_rot").get().setRotY(rot);
        model.getBone("neck_rot").get().setRotY(rot);



    }



}


