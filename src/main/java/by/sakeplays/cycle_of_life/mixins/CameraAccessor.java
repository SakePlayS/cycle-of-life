package by.sakeplays.cycle_of_life.mixins;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)

public interface CameraAccessor {

    @Invoker("setPosition")
    void callSetPosition(double x, double y, double z);

    @Invoker("setRotation")
    void callSetRotation(float yRot, float xRot, float roll);

    @Accessor("eyeHeight")
    float accessorGetEyeHeight();

    @Accessor("yRot")
    float accessorGetYRot();

    @Accessor("xRot")
    float accessorGetXRot();

    @Accessor("roll")
    float accessorGetRoll();

    @Accessor("eyeHeightOld")
    float accessorGetEyeHeightOld();
}
