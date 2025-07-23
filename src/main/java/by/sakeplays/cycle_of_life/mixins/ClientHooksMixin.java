package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

        Player player = Minecraft.getInstance().player;

        if (player == null) return 4F;

        float scale = 0;

        scale = switch (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()) {
            case 1 -> 1.2f;
            case 2 -> 1f;
            default -> 1f;
        };

        return 3F * scale * (0.1f + (Math.max(0f, Minecraft.getInstance().player.getData(DataAttachments.DINO_DATA).getGrowth() - 0.1f)));
    }

}
