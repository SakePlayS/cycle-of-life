package by.sakeplays.cycle_of_life.client.block.deinonychus_nest;

import by.sakeplays.cycle_of_life.block.DeinonychusNestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DeinonychusNestRenderer extends GeoBlockRenderer<DeinonychusNestBlockEntity> {
    public DeinonychusNestRenderer(BlockEntityRendererProvider.Context context) {
        super(new DeinonychusNestModel());
    }

    @Override
    public int getPackedOverlay(DeinonychusNestBlockEntity animatable, float u, float partialTick) {
        return super.getPackedOverlay(animatable, u, partialTick);
    }


}
