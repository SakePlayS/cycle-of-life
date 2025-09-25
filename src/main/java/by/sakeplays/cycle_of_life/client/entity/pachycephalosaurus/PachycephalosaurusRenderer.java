package by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus;

import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
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
    public long getInstanceId(Pachycephalosaurus animatable) {
        return animatable.getPlayer().getId();
    }

    @Override
    public void preRender(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        animatable.scale = Util.calculateScale(animatable, 0.1f, 1.015f);

        poseStack.scale(animatable.scale, animatable.scale, animatable.scale);
    }

    @Override
    protected void renderNameTag(Pachycephalosaurus entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {

    }

    @Override
    public void postRender(PoseStack poseStack, Pachycephalosaurus animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        if (animatable.isBody()) return;

        GeoBone head = model.getBone("head_center").get();
        GeoBone body2 = model.getBone("body2_center").get();
        GeoBone body1 = model.getBone("body1_center").get();
        GeoBone tail1 = model.getBone("tail1_center").get();
        GeoBone tail2 = model.getBone("tail2_center").get();

        Player player = animatable.getPlayer();

        ClientHitboxData.updateHitboxes(head, body1, body2, tail1, tail2, player, partialTick);
    }
}


