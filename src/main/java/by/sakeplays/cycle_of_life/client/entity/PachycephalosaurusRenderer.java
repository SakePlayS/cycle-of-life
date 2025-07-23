package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PachycephalosaurusRenderer extends GeoEntityRenderer<Pachycephalosaurus> {


    public PachycephalosaurusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PachycephalosaurusModel());
    }

    @Override
    public long getInstanceId(Pachycephalosaurus animatable) {
        return animatable.getPlayer().getId();
    }

    @Override
    public void preRender(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        float size = Util.calculateGrowth(animatable, 0.05f, 1.1f);

        poseStack.scale(size, size, size);
    }

    @Override
    protected void renderNameTag(Pachycephalosaurus entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {

    }
}


