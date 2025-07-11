package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class CameraMixin {

    private static double smoothedYOffset = 0;

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyHitboxSize(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        // Only run this for actual players
        if ((Object)this instanceof Player player) {
            float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();
            int dinoID = Util.getDino(player).getID();

            float baseHeight = switch (dinoID) {
                case 1 -> 2.5F;
                case 2 -> 1.4F;
                default -> 1.8F;
            };

            float baseWidth = switch (dinoID) {
                case 1 -> 0.8F;
                case 2 -> 0.55F;
                default -> 0.6F;
            };

            float scale = 0.1f + Math.max(0f, growth - 0.1f);

            cir.setReturnValue(EntityDimensions.scalable(baseWidth * scale, baseHeight * scale));
        }
    }
}
