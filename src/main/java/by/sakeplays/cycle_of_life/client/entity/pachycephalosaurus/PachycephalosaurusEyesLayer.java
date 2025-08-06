package by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus;

import by.sakeplays.cycle_of_life.client.ModRenderTypes;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.SkinData;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import by.sakeplays.cycle_of_life.util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PachycephalosaurusEyesLayer<T extends Entity & GeoAnimatable> extends GeoRenderLayer<Pachycephalosaurus> {

    public PachycephalosaurusEyesLayer(GeoRenderer<Pachycephalosaurus> renderer) {
        super(renderer);
    }

    ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("cycle_of_life", "textures/entity/pachycephalosaurus/eyes.png");

    @Override
    public void render(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        int color;
        SkinData data;
        if (!animatable.isBody()) {
            data = animatable.getPlayer().getData(DataAttachments.SKIN_DATA);
            color = animatable.isForScreenRendering ? animatable.eyesColor : data.getEyesColor();

        } else {
            color = animatable.getEyesColor();
        }


        poseStack.pushPose();
        poseStack.scale(1/animatable.scale, 1/animatable.scale, 1/animatable.scale);

        getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                ModRenderTypes.grayscaleTinted(texture),
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                partialTick,
                packedLight,
                packedOverlay,
                color
        );

        poseStack.popPose();
    }


}
