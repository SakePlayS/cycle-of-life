package by.sakeplays.cycle_of_life.client.entity.deinonychus;

import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import by.sakeplays.cycle_of_life.entity.MeatChunkEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class DeinonychusRenderer extends GeoEntityRenderer<Deinonychus>  {
    private int tick = 0;


    public DeinonychusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeinonychusModel());
        this.addRenderLayer(new DeinonychusMaleDisplayLayer<>(this));
        this.addRenderLayer(new DeinonychusBellyLayer<>(this));
        this.addRenderLayer(new DeinonychusBodyLayer<>(this));
        this.addRenderLayer(new DeinonychusEyesLayer<>(this));
        this.addRenderLayer(new DeinonychusFlankLayer<>(this));
        this.addRenderLayer(new DeinonychusMarkingsLayer<>(this));

    }

    @Override
    public void preRender(PoseStack poseStack, Deinonychus animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        float size = Util.calculateGrowth(animatable, 0.04f, 0.8f);

        poseStack.scale(size, size, size);


        boolean isMale = !animatable.isBody() ? animatable.getPlayer().getData(DataAttachments.DINO_DATA).isMale() : animatable.isMale();
        model.getBone("male_display1").get().setHidden(false);
        model.getBone("male_display2").get().setHidden(false);
        model.getBone("male_display3").get().setHidden(false);


        model.getBone("male_display1").get().setHidden(!isMale);
        model.getBone("male_display2").get().setHidden(!isMale);
        model.getBone("male_display3").get().setHidden(!isMale);

    }


    @Override
    public void postRender(PoseStack poseStack, Deinonychus animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        if (animatable.isBody()) return;

        GeoBone head = model.getBone("head_center").get();
        GeoBone body2 = model.getBone("body2_center").get();
        GeoBone body1 = model.getBone("body1_center").get();
        GeoBone tail1 = model.getBone("tail1_center").get();
        GeoBone tail2 = model.getBone("tail2_center").get();

        Player player = animatable.getPlayer();

        ClientHitboxData.updateHitboxes(head, body1, body2, tail1, tail2, player, partialTick);



        if (true) {
            GeoBone mouthBone = this.getGeoModel().getAnimationProcessor().getBone("grab_handler");
            if (mouthBone != null) {
                poseStack.pushPose();
                Entity meatChunk = new MeatChunkEntity(COLEntities.MEAT_CHUNK.get(), player.level());

                this.moveToBone(poseStack, mouthBone);
                float scale = (float) Math.cbrt(player.getData(DataAttachments.DINO_DATA).getCarriedMeatSize());

                poseStack.scale(scale, scale, scale);
             //   Minecraft.getInstance().getEntityRenderDispatcher().render
             //           (meatChunk, mouthBone.getWorldPosition().x() / scale, mouthBone.getWorldPosition().y() / scale,
             //                  mouthBone.getWorldPosition().z() / scale, 0, partialTick, poseStack, bufferSource, packedLight);

                poseStack.popPose();
            }
        }
    }


    @Override
    protected void renderNameTag(Deinonychus entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
    }


    private void moveToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
        poseStack.mulPose(Axis.XP.rotationDegrees(bone.getRotX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(bone.getRotY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(bone.getRotZ()));
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }
}
