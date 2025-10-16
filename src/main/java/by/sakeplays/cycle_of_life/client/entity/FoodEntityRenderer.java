package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.entity.FoodEntity;
import by.sakeplays.cycle_of_life.util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FoodEntityRenderer extends GeoEntityRenderer<FoodEntity> {

    public FoodEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FoodEntityModel());
    }

    @Override
    public void preRender(PoseStack poseStack, FoodEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

    }

    @Override
    public void renderRecursively(PoseStack poseStack, FoodEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        float scaleFactor = animatable.getRemainingFood() < 1 ? Mth.lerp(animatable.getRemainingFood()*animatable.getRemainingFood(), 0.2f, 1f) : (float) Math.pow(animatable.getRemainingFood(), 0.33f);

        Util.renderItemStack(poseStack, animatable.level(), packedLight, bufferSource, animatable.getFoodType().getItemForTexture(),
                -0.05 * scaleFactor,0.2f * scaleFactor,-0.2 * scaleFactor,
                1 * scaleFactor,1 * scaleFactor,3f  * scaleFactor,
                90, 0, 0);
    }

    @Override
    protected void renderNameTag(FoodEntity entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {}
}


