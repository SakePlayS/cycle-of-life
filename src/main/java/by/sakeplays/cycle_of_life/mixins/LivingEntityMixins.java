package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.FoodEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.network.bidirectional.SendJumpAnimFlag;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.network.PacketDistributor;
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

            float baseHeight = Math.max(0.01f, Util.getDinoBaseHeight(player));
            float baseWidth = Math.max(0.01f, Util.getDinoBaseWidth(player));

            if (player.getData(DataAttachments.DINO_DATA).isFlying()) baseHeight = baseHeight / 3;

            float scale = Util.getDino(player).getGrowthCurve().calculate(growth, GrowthCurveStat.SCALE);

            cir.setReturnValue(EntityDimensions.scalable(baseWidth * scale, baseHeight * scale));
        } else if ((Object)this instanceof FoodEntity foodEntity) {

            float scale = foodEntity.getRemainingFood() < 1 ? Mth.lerp(foodEntity.getRemainingFood()*foodEntity.getRemainingFood(), 0.4f, 1f) : (float) Math.pow(foodEntity.getRemainingFood(), 0.33f);

            cir.setReturnValue(EntityDimensions.scalable(0.3f * scale, 0.1f * scale));

        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void jump(CallbackInfo ci) {
        if ((Object)this instanceof Player player) {
            DinoData data = player.getData(DataAttachments.DINO_DATA);

            if (!data.isInBuildMode() && Util.getDino(player) != Dinosaurs.NONE) {
                ci.cancel();
            }
        }
    }
}
