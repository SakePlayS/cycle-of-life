package by.sakeplays.cycle_of_life.mixins;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Suppress the rendering of all HUD elements aside from chat.

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void cancelHotbarRender(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderHealthLevel", at = @At("HEAD"), cancellable = true)
    private void cancelHealthRender(GuiGraphics graphics, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void cancelFoodRender(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        ci.cancel();
    }
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void cancelXPRender(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        ci.cancel();
    }
}