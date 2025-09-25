package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class CancelPlayerDamage {

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    protected void cancelDamage(DamageSource damageSrc, float damageAmount, CallbackInfo ci) {
        if ((Object)this instanceof Player player) {
            ci.cancel();
        }
    }


    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void cancelHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof Player player) {

            if (!player.getData(DataAttachments.DINO_DATA).isInBuildMode()) {

                cir.setReturnValue(false);
            }
        }
    }
}
