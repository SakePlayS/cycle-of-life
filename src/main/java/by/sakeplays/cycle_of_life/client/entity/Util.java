package by.sakeplays.cycle_of_life.client.entity;

import by.sakeplays.cycle_of_life.entity.Pachycephalosaurus;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class Util {


    public static void handleBodyRotation(BakedGeoModel model, Pachycephalosaurus animatable) {
        float playerRot = animatable.getPlayer().getYRot();
        float boneRot =  model.getBone("root").get().getRotY() * Mth.RAD_TO_DEG;
        float boneRotRad = model.getBone("root").get().getRotY();
        float difference = playerRot + boneRot;


        model.getBone("root").get().setRotY(boneRotRad + -Mth.DEG_TO_RAD * difference/7);
    }

}
