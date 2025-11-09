package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public class CancelPlayerDamage {

    private List<ResourceKey<DamageType>> ignoredDamageTypes = List.of(DamageTypes.FALL, DamageTypes.STARVE, DamageTypes.DROWN);


    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void cancelHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof Player player) {

            if (!player.getData(DataAttachments.DINO_DATA).isInHumanMode()) {

                if (!player.level().isClientSide) {

                    for (ResourceKey<DamageType> type : ignoredDamageTypes) {
                        if (source.is(type)) {
                            cir.setReturnValue(false);
                            return;
                        }
                    }

                    if (player.getData(DataAttachments.VANILLA_IFRAME_COMPAT_UNTILL) > player.tickCount) {
                        cir.setReturnValue(false);
                        return;
                    }

                    player.setData(DataAttachments.VANILLA_IFRAME_COMPAT_UNTILL, player.getData(DataAttachments.VANILLA_IFRAME_COMPAT_UNTILL) + 10);
                    Util.dealDamage(player, amount * 4, 0, true);
                }

                cir.setReturnValue(false);
            }
        }
    }
}
