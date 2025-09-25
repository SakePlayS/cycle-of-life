package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixins {

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyHitboxSize(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((Object)this instanceof Player player) {
            if (player == null) return;

            DinoData data = player.getData(DataAttachments.DINO_DATA);

            if (data.isInBuildMode()) {
                if (!data.isBuildModeUpdated()) cir.setReturnValue(EntityDimensions.scalable(0.6f, 1.8f));
                return;
            }


            float growth = data.getGrowth();
            int dinoID = Util.getDino(player).getID();

            float baseHeight = switch (dinoID) {
                case 1 -> 1.9F;
                case 2 -> 1.2F;
                case 3 -> 1.1F;
                default -> 1.8F;
            };

            float baseWidth = switch (dinoID) {
                case 1 -> 0.8F;
                case 2 -> 0.55F;
                case 3 -> 0.5F;

                default -> 0.6F;
            };

            if (player.getData(DataAttachments.DINO_DATA).getFlightState() != 0) baseHeight = baseHeight / 3;

            float scale = 0.1f + Math.max(0f, growth - 0.1f);

            cir.setReturnValue(EntityDimensions.scalable(baseWidth * scale, baseHeight * scale));
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void jump(CallbackInfo ci) {
        if ((Object)this instanceof Player player) {
            DinoData data = player.getData(DataAttachments.DINO_DATA);

            if (!data.isInBuildMode() && Util.getDino(player) != Dinosaurs.NONE) {
                ci.cancel();

                if (Util.getDino(player) == Dinosaurs.PTERANODON) return;

                float baseJumpStrength = Util.getDino(player).getJumpStrength();

                float dinoJumpStrength = (float) (baseJumpStrength * Mth.lerp(Math.pow(data.getGrowth(), 0.625), 0.2f, 1f));

                if (dinoJumpStrength > 0.25f && player.getData(DataAttachments.KNOCKDOWN_TIME) < 0 && !data.isLayingEggs()) {
                    player.setDeltaMovement(
                            player.getDeltaMovement().x,
                            player.getDeltaMovement().y + dinoJumpStrength,
                            player.getDeltaMovement().z
                    );
                }
            }
        }
    }
}
