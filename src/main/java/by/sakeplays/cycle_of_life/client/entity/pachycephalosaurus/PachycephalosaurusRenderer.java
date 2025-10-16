package by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PachycephalosaurusRenderer extends GeoEntityRenderer<Pachycephalosaurus> {


    public PachycephalosaurusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PachycephalosaurusModel());
        this.addRenderLayer(new PachycephalosaurusBellyLayer<>(this));
        this.addRenderLayer(new PachycephalosaurusBodyLayer<>(this));
        this.addRenderLayer(new PachycephalosaurusEyesLayer<>(this));
        this.addRenderLayer(new PachycephalosaurusFlankLayer<>(this));
        this.addRenderLayer(new PachycephalosaurusMaleDisplayLayer<>(this));
        this.addRenderLayer(new PachycephalosaurusMarkingsLayer<>(this));
    }



    @Override
    public void preRender(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        animatable.scale = Util.calculateScale(animatable);

        poseStack.scale(animatable.scale, animatable.scale, animatable.scale);
    }

    @Override
    protected void renderNameTag(Pachycephalosaurus entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {

    }

    @Override
    public void postRender(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
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
    public void renderFinal(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);

        if (animatable.isCorpse() || animatable.getPlayer() == null || animatable.isForScreenRendering) return;

        GeoBone tail_root = model.getBone("tail_root").get();

        animatable.tailRootPos = new Position(
                animatable.getPlayer().getX() + tail_root.getWorldPosition().x,
                animatable.getPlayer().getY() + tail_root.getWorldPosition().y,
                animatable.getPlayer().getZ() + tail_root.getWorldPosition().z
                );
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Pachycephalosaurus animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        if (!animatable.isCorpse() && animatable.getPlayer() != null && animatable.getPlayer().getData(DataAttachments.HELD_FOOD_DATA).getHeldFood() != DinosaurFood.FOOD_NONE) {
            GeoBone mouthBone = this.getGeoModel().getAnimationProcessor().getBone("item_display");
            if (mouthBone == bone) {
                Util.renderItemStack(poseStack, animatable.level(), packedLight, bufferSource,
                        animatable.getPlayer().getData(DataAttachments.HELD_FOOD_DATA).getHeldFood().getItemForTexture(),
                        -0.1f, 1.7, -1.7f,
                        0.9f, 0.9f, 3,
                        90f, 0f, -30f);
            }
        }
    }

}


