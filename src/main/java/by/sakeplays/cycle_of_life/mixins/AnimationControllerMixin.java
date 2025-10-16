package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.client.entity.CrossfadeTickTracker;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeLocation;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.value.Constant;

import java.util.List;

@Mixin(AnimationController.class)
public class AnimationControllerMixin {


    @Inject(method = "getCurrentKeyFrameLocation", at = @At("HEAD"), cancellable = true)
    private void modifyCurrentKeyframeLocation(List<Keyframe<MathValue>> frames,
                                               double ageInTicks, CallbackInfoReturnable<KeyframeLocation<Keyframe<MathValue>>> cir) {

        if (ageInTicks == 0) {
            AnimationController<?> controller = (AnimationController<?>)(Object)(this);
            AnimationControllerAccessor accessor = (AnimationControllerAccessor)controller;


            if (accessor.getAnimationState() == AnimationController.State.TRANSITIONING && accessor.getAnimatable() instanceof DinosaurEntity entity && entity.getPlayer() != null) {

                double totalFrameTime = 0;
                double newAgeInTicks = CrossfadeTickTracker.getCrossfadeTick(entity.getPlayer().getId());

                for (Keyframe<MathValue> frame : frames) {
                    totalFrameTime += frame.length();

                    if (totalFrameTime > newAgeInTicks) {
                        cir.setReturnValue(new KeyframeLocation<>(frame, (newAgeInTicks - (totalFrameTime - frame.length()))));
                        return;
                    }
                }

                cir.setReturnValue(new KeyframeLocation<>(frames.getLast(), newAgeInTicks));
            }
        }
    }
}
