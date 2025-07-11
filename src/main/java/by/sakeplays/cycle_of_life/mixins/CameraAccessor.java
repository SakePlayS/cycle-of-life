package by.sakeplays.cycle_of_life.mixins;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)

public interface CameraAccessor {

    @Invoker("setPosition")
    void callSetPosition(double x, double y, double z);
}
