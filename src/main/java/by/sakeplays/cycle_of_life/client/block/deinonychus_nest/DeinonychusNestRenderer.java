package by.sakeplays.cycle_of_life.client.block.deinonychus_nest;

import by.sakeplays.cycle_of_life.block.DeinonychusNestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.ArrayList;
import java.util.List;

public class DeinonychusNestRenderer extends GeoBlockRenderer<DeinonychusNestBlockEntity> {
    public DeinonychusNestRenderer(BlockEntityRendererProvider.Context context) {
        super(new DeinonychusNestModel());
    }

    @Override
    public int getPackedOverlay(DeinonychusNestBlockEntity animatable, float u, float partialTick) {
        return super.getPackedOverlay(animatable, u, partialTick);
    }

    @Override
    public void preRender(PoseStack poseStack, DeinonychusNestBlockEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        List<GeoBone> eggBones = new ArrayList<>();

        eggBones.add(model.getBone("egg_1").get());
        eggBones.add(model.getBone("egg_2").get());
        eggBones.add(model.getBone("egg_3").get());
        eggBones.add(model.getBone("egg_4").get());
        eggBones.add(model.getBone("egg_5").get());

        for (GeoBone egg : eggBones) egg.setHidden(true);

        int eggs = animatable.getEggsCount();

        for (int i = 0; i < eggs && i < eggBones.size(); i++) {
            eggBones.get(i).setHidden(false);
        }

    }
}
