package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
    protected float getShadowRadius(Pachycephalosaurus entity) {
        return 0.1f;
    }
}


