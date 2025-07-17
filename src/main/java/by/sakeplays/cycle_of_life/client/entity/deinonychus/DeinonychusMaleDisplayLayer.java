package by.sakeplays.cycle_of_life.client.entity.deinonychus;

import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.client.ModRenderTypes;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.SkinData;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DeinonychusMaleDisplayLayer<T extends Entity & GeoAnimatable> extends GeoRenderLayer<Deinonychus> {

    public DeinonychusMaleDisplayLayer(GeoRenderer<Deinonychus> renderer) {
        super(renderer);
    }

    ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("cycle_of_life", "textures/entity/deinonychus/male_display.png");

    @Override
    public void render(PoseStack poseStack, Deinonychus animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        int color;
        SkinData data;
        if (!animatable.isBody()) {
             data = animatable.getPlayer().getData(DataAttachments.SKIN_DATA);
             color = animatable.isForScreenRendering ? animatable.maleDisplayColor : data.getMaleDisplayColor();
            if (!animatable.isForScreenRendering) color = animatable.getPlayer().getData(DataAttachments.DINO_DATA).isMale()
                    ? color : data.getMarkingsColor();

        } else {
            color = animatable.getMaleDisplayColor();
            if (!animatable.isMale()) color = animatable.getMarkingsColor();
        }


        float size = Util.calculateGrowth(animatable);

        poseStack.pushPose();
        poseStack.scale(1/size, 1/size, 1/size);

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
