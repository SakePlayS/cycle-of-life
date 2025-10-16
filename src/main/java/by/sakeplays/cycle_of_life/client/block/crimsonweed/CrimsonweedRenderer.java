package by.sakeplays.cycle_of_life.client.block.crimsonweed;

import by.sakeplays.cycle_of_life.block.DeinonychusNestBlockEntity;
import by.sakeplays.cycle_of_life.block.herbi_food.CrimsonweedBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.ArrayList;
import java.util.List;

public class CrimsonweedRenderer extends GeoBlockRenderer<CrimsonweedBlockEntity> {
    public CrimsonweedRenderer(BlockEntityRendererProvider.Context context) {
        super(new CrimsonweedModel());
    }

    @Override
    public int getPackedOverlay(CrimsonweedBlockEntity animatable, float u, float partialTick) {
        return super.getPackedOverlay(animatable, u, partialTick);
    }
}
