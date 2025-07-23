package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.entity.MeatChunkEntity;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MeatChunkRenderer extends GeoEntityRenderer<MeatChunkEntity> {

    public MeatChunkRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MeatChunkModel());
    }

    @Override
    public void preRender(PoseStack poseStack, MeatChunkEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

    }
}


