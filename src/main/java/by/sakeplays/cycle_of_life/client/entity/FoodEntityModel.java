package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.entity.FoodEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FoodEntityModel extends GeoModel<FoodEntity> {
    @Override
    public ResourceLocation getModelResource(FoodEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/meat_chunk.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FoodEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/entity/meat_chunk.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FoodEntity animatable) {
        return null;
    }

}
