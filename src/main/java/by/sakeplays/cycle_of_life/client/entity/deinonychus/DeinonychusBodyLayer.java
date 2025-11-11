package by.sakeplays.cycle_of_life.client.entity.deinonychus;

import by.sakeplays.cycle_of_life.client.ModRenderTypes;
import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.SkinData;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
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

public class DeinonychusBodyLayer<T extends Entity & GeoAnimatable> extends GeoRenderLayer<Deinonychus> {

    public DeinonychusBodyLayer(GeoRenderer<Deinonychus> renderer) {
        super(renderer);
    }

    ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("cycle_of_life", "textures/entity/deinonychus/body.png");

    @Override
    public void render(PoseStack poseStack, Deinonychus animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType,
                       MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        int primaryColor;
        int secondaryColor;
        SkinData data;
        if (!animatable.isCorpse()) {
            data = animatable.getPlayer().getData(DataAttachments.SKIN_DATA);
            primaryColor = animatable.isForScreenRendering ? animatable.colors.getColor(ColorableBodyParts.BODY).first() : data.getColor(ColorableBodyParts.BODY).first();
            secondaryColor = animatable.isForScreenRendering ? animatable.colors.getColor(ColorableBodyParts.BODY).second() : data.getColor(ColorableBodyParts.BODY).second();
        } else {
            primaryColor = animatable.getColors().getColor(ColorableBodyParts.BODY).first();
            secondaryColor = animatable.getColors().getColor(ColorableBodyParts.BODY).second();
        }

        Util.applyBodyPartColors(primaryColor, secondaryColor);

        poseStack.pushPose();
        poseStack.scale(1/animatable.scale, 1/animatable.scale, 1/animatable.scale);

        getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                ModRenderTypes.grayscaleTinted(texture),
                bufferSource.getBuffer(ModRenderTypes.grayscaleTinted(texture)),
                partialTick,
                packedLight,
                packedOverlay,
               0xFFFFFFFF
        );

        if (bufferSource instanceof MultiBufferSource.BufferSource buf) {
            buf.endBatch();
        }

        poseStack.popPose();
    }
}
