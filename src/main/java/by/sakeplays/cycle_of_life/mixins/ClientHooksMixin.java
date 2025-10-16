package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.util.Util;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
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

        if (player == null || player.getData(DataAttachments.DINO_DATA).isInBuildMode()) return 4F;

        float scale = 0;

        scale = switch (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()) {
            case 1 -> 1.2f;
            case 2 -> 1f;
            default -> 1f;
        };

        float dzSpeed = (float)(Math.sqrt(0.35f + player.getDeltaMovement().length()));

        if (!player.getData(DataAttachments.DINO_DATA).isFlying()) dzSpeed = 1;

        float size = Util.getDino(player).getGrowthCurve().calculate(player.getData(DataAttachments.DINO_DATA).getGrowth(), GrowthCurveStat.SCALE);

        return Math.max(0.5f, 3F * scale * dzSpeed * (0.1f + (Math.max(0f, size - 0.025f))));
    }

}
