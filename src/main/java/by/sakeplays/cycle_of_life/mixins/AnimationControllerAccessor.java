package by.sakeplays.cycle_of_life.mixins;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeLocation;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.List;

@Mixin(AnimationController.class)
public interface AnimationControllerAccessor {

    @Accessor("tickOffset")
    double getTickOffset();


    @Accessor("animatable")
    GeoAnimatable getAnimatable();

    @Accessor("animationState")
    AnimationController.State getAnimationState();

    @Accessor("tickOffset")
    void setTickOffset(double val);

    @Accessor("shouldResetTick")
    void setShouldResetTick(boolean val);
}
