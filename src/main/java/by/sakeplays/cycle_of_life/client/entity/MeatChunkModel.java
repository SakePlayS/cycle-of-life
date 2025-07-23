package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.MeatChunkEntity;
import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MeatChunkModel extends GeoModel<MeatChunkEntity> {
    @Override
    public ResourceLocation getModelResource(MeatChunkEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/meat_chunk.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MeatChunkEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/entity/meat_chunk.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MeatChunkEntity animatable) {
        return null;
    }

}
