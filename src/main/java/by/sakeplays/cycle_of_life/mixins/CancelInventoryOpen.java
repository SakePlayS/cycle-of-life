package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Minecraft.class)
public class CancelInventoryOpen {

    @Redirect(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z"
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/Options;keyInventory:Lnet/minecraft/client/KeyMapping;"
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/Options;keyAdvancements:Lnet/minecraft/client/KeyMapping;"
                    )
            )
    )
    private boolean blockInventoryConsume(KeyMapping self) {
        Minecraft mc = (Minecraft)(Object)this;
        if (mc.player != null && !mc.player.getData(DataAttachments.DINO_DATA).isInHumanMode()) {
            return false;
        }
        return self.consumeClick();
    }
}
