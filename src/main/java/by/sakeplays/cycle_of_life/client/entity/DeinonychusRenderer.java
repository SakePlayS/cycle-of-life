package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class DeinonychusRenderer extends GeoEntityRenderer<Deinonychus>  {
    public DeinonychusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeinonychusModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Deinonychus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        float growth = animatable.getPlayer().getData(DataAttachments.DINO_DATA).getGrowth();

        float size = Mth.lerp(growth, 0.04f, 0.9f);

        poseStack.scale(size, size, size);

    }



    private void handleTailPhysics(BakedGeoModel model, Deinonychus animatable, float partialTick) {

    }

    @Override
    public long getInstanceId(Deinonychus animatable) {
        return animatable.getPlayer().getId();
    }




}
