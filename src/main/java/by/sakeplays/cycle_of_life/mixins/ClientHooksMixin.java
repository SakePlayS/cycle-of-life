package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.ClientHooks;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientHooks.class)
public class ClientHooksMixin {

    @ModifyReturnValue(method = "getDetachedCameraDistance", at = @At("RETURN"))
    private static float modifyDefaultCameraDistance(float original) {

        if (Minecraft.getInstance().player == null) return 4F;

        return 3F * (0.1f + (Math.max(0f, Minecraft.getInstance().player.getData(DataAttachments.DINO_DATA).getGrowth() - 0.1f)));
    }

}
