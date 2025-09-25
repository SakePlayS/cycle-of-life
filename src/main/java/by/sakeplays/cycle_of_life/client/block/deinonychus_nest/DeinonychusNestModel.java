package by.sakeplays.cycle_of_life.client.block.deinonychus_nest;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.block.DeinonychusNestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DeinonychusNestModel extends GeoModel<DeinonychusNestBlockEntity> {
    @Override
    public ResourceLocation getModelResource(DeinonychusNestBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/block/deinonychus_nest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DeinonychusNestBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/block/deinonychus_nest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DeinonychusNestBlockEntity animatable) {
        return null;
    }




}
