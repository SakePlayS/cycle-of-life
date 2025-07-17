package by.sakeplays.cycle_of_life.client.entity.deinonychus;

import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
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

        float size = Util.calculateGrowth(animatable);

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

        Player player = animatable.getPlayer();

        Vec3 interpolatedPos = new Vec3(Mth.lerp(partialTick, player.xOld, player.getX()),
        Mth.lerp(partialTick, player.yOld, player.getY()),
                Mth.lerp(partialTick, player.zOld, player.getZ()));

        if (tick != animatable.getPlayer().tickCount) tick(animatable, model, interpolatedPos);

    }


    private void tick(Deinonychus animatable, BakedGeoModel model, Vec3 interpolatedPos ) {

        if (animatable.isBody()) return;
        if (animatable.isForScreenRendering) return;

        tick = animatable.getPlayer().tickCount;

        if (animatable.getPlayer() == Minecraft.getInstance().player) {
            Vector3d headPos = model.getBone("head_center").get().getWorldPosition();
            Vector3d body2Pos = model.getBone("body2_center").get().getWorldPosition();
            Vector3d body1Pos = model.getBone("body1_center").get().getWorldPosition();
            Vector3d tail1Pos = model.getBone("tail1_center").get().getWorldPosition();
            Vector3d tail2Pos = model.getBone("tail2_center").get().getWorldPosition();

            Vec3 headWorldPos = interpolatedPos.add(headPos.x, headPos.y, headPos.z);
            Vec3 body1WorldPos = interpolatedPos.add(body1Pos.x, body1Pos.y, body1Pos.z);
            Vec3 body2WorldPos = interpolatedPos.add(body2Pos.x, body2Pos.y, body2Pos.z);
            Vec3 tail1WorldPos = interpolatedPos.add(tail1Pos.x, tail1Pos.y, tail1Pos.z);
            Vec3 tail2WorldPos = interpolatedPos.add(tail2Pos.x, tail2Pos.y, tail2Pos.z);


            animatable.getPlayer().getData(DataAttachments.HITBOX_DATA)
                    .setHeadHitboxPos(new Position(headWorldPos.x, headWorldPos.y, headWorldPos.z));

            animatable.getPlayer().getData(DataAttachments.HITBOX_DATA)
                    .setBody1Pos(new Position(body1WorldPos.x, body1WorldPos.y, body1WorldPos.z));

            animatable.getPlayer().getData(DataAttachments.HITBOX_DATA)
                    .setBody2Pos(new Position(body2WorldPos.x, body2WorldPos.y, body2WorldPos.z));

            animatable.getPlayer().getData(DataAttachments.HITBOX_DATA)
                    .setTail1Pos(new Position(tail1WorldPos.x, tail1WorldPos.y, tail1WorldPos.z));

            animatable.getPlayer().getData(DataAttachments.HITBOX_DATA)
                    .setTail2Pos(new Position(tail2WorldPos.x, tail2WorldPos.y, tail2WorldPos.z));
        }
    }

    @Override
    protected void renderNameTag(Deinonychus entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
    }
}
