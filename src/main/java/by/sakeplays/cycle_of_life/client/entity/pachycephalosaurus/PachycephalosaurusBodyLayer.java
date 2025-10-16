package by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus;

import by.sakeplays.cycle_of_life.client.ModRenderTypes;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.SkinData;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
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

public class PachycephalosaurusBodyLayer<T extends Entity & GeoAnimatable> extends GeoRenderLayer<Pachycephalosaurus> {

    public PachycephalosaurusBodyLayer(GeoRenderer<Pachycephalosaurus> renderer) {
        super(renderer);
    }

    ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("cycle_of_life", "textures/entity/pachycephalosaurus/body.png");

    @Override
    public void render(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        int color;
        SkinData data;
        if (!animatable.isCorpse()) {
            data = animatable.getPlayer().getData(DataAttachments.SKIN_DATA);
            color = animatable.isForScreenRendering ? animatable.bodyColor : data.getBodyColor();

        } else {
            color = animatable.getBodyColor();
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
