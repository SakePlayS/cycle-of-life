package by.sakeplays.cycle_of_life.client.entity.pteranodon;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.Pteranodon;
import by.sakeplays.cycle_of_life.util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class PteranodonRenderer extends GeoEntityRenderer<Pteranodon>  {
    private int tick = 0;


    public PteranodonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PteranodonModel());


    }

    @Override
    public void preRender(PoseStack poseStack, Pteranodon animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        animatable.scale = Util.calculateScale(animatable);

        poseStack.scale(animatable.scale, animatable.scale, animatable.scale);

        boolean isMale = !animatable.isCorpse() ? animatable.getPlayer().getData(DataAttachments.DINO_DATA).isMale() : animatable.isMale();


    }


    @Override
    public void postRender(PoseStack poseStack, Pteranodon animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        if (animatable.isCorpse()) return;

        GeoBone head = model.getBone("head_center").get();
        GeoBone body2 = model.getBone("body2_center").get();
        GeoBone body1 = model.getBone("body1_center").get();
        GeoBone tail1 = model.getBone("tail1_center").get();
        GeoBone tail2 = model.getBone("tail2_center").get();

        Player player = animatable.getPlayer();

        ClientHitboxData.updateHitboxes(head, body1, body2, tail1, tail2, player, partialTick);

    }


    @Override
    protected void renderNameTag(Pteranodon entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
    }


    private void moveToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
        poseStack.mulPose(Axis.XP.rotationDegrees(bone.getRotX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(bone.getRotY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(bone.getRotZ()));
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }
}
