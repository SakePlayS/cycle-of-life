package by.sakeplays.cycle_of_life.client.block.crimsonweed;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.block.DeinonychusNestBlockEntity;
import by.sakeplays.cycle_of_life.block.herbi_food.CrimsonweedBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class CrimsonweedModel extends GeoModel<CrimsonweedBlockEntity> {
    @Override
    public ResourceLocation getModelResource(CrimsonweedBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "geo/block/crimsonweed.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CrimsonweedBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "textures/block/crimsonweed.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CrimsonweedBlockEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(CrimsonweedBlockEntity animatable, long instanceId, AnimationState<CrimsonweedBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone root = getBone("root").get();

        root.setRotY(animatable.getRotationOffset());
        root.setPosX(animatable.getXOffset());
        root.setPosZ(animatable.getZOffset());

        root.setScaleX(1.2f + animatable.getSizeOffset());
        root.setScaleY(1.2f + animatable.getSizeOffset());
        root.setScaleZ(1.2f + animatable.getSizeOffset());

        if (animatable.getRemainingFood() <= 0) {
            root.setScaleX(0);
            root.setScaleY(0);
            root.setScaleZ(0);
        }

    }
}
