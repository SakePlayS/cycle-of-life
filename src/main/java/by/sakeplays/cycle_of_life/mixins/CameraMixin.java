package by.sakeplays.cycle_of_life.mixins;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurve;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.event.client.HandleKeys;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {


    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double targetX = 0;
    private double targetY = 0;
    private double targetZ = 0;

    @Inject(
            method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private void adjust(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse,
                                                    float partialTick, CallbackInfo ci) {

        CameraAccessor accessor = (CameraAccessor) (Camera)(Object)(this);

        Dinosaurs selectedDino = Dinosaurs.getById(entity.getData(DataAttachments.DINO_DATA).getSelectedDinosaur());

        if (selectedDino == null) return;

        float yRot = accessor.accessorGetYRot() * Mth.DEG_TO_RAD;

        double ex = Mth.lerp((double)partialTick, entity.xo, entity.getX());
        double ey = Mth.lerp((double)partialTick, entity.yo, entity.getY()) + Math.max(0.101d, (double)Mth.lerp(partialTick, accessor.accessorGetEyeHeightOld(), accessor.accessorGetEyeHeight()));
        double ez = Mth.lerp((double)partialTick, entity.zo, entity.getZ());


        float f1 = 0;
        float f2 = 0;

        float camDistanceFactor = selectedDino.getCameraDistance() *
                Math.max(0.1f, selectedDino.getGrowthCurve().calculate(entity.getData(DataAttachments.DINO_DATA).getGrowth(), GrowthCurveStat.SCALE));

        f1 = (float) Math.sin(yRot + 1.578888f) * camDistanceFactor;
        f2 = -(float) Math.cos(yRot + 1.578888f) * camDistanceFactor;

        switch (HandleKeys.cameraMode) {
            case 1 -> {
                f1 = 0;
                f2 = 0;
            }

            case 2 -> {
                f1 = -f1;
                f2 = -f2;
            }
        }

        if (!Util.hasClearLineOfSight(new Position(ex + f1, ey, ez + f2), new Position(ex, ey, ez), entity.level())) {
            f1 = 0;
            f2 = 0;
        }

        targetX = ex + f1;
        targetY = ey;
        targetZ = ez + f2;

        float dt = Math.min(1.0f, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());

        this.x = this.x + ((targetX - this.x) * dt * 0.6f);
        this.y = this.y + ((targetY - this.y) * dt * 0.6f);
        this.z = this.z + ((targetZ - this.z) * dt * 0.6f);

        float xoff = xOffset(this.x, this.y, this.z, entity.level());
        float zoff = zOffset(this.x, this.y, this.z, entity.level());

        accessor.callSetPosition(this.x + xoff, this.y, this.z + zoff);
    }

    // forgive me but i cant be bothered
    private float xOffset(double x, double y, double z, Level level) {
        if (level.getBlockState(BlockPos.containing(x + 0.101f, y, z + 0.101f)).isSolid()) return -0.101f;
        if (level.getBlockState(BlockPos.containing(x - 0.101f, y, z + 0.101f)).isSolid()) return 0.101f;
        if (level.getBlockState(BlockPos.containing(x + 0.101f, y, z - 0.101f)).isSolid()) return -0.101f;
        if (level.getBlockState(BlockPos.containing(x - 0.101f, y, z - 0.101f)).isSolid()) return 0.101f;

        if (level.getBlockState(BlockPos.containing(x + 0.101f, y, z)).isSolid()) return -0.101f;
        if (level.getBlockState(BlockPos.containing(x - 0.101f, y, z)).isSolid()) return 0.101f;

        return 0f;
    }

    private float zOffset(double x, double y, double z, Level level) {
        if (level.getBlockState(BlockPos.containing(x-0.101f, y, z + 0.101f)).isSolid()) return -0.101f;
        if (level.getBlockState(BlockPos.containing(x-0.101f, y, z - 0.101f)).isSolid()) return 0.101f;
        if (level.getBlockState(BlockPos.containing(x+0.101f, y, z + 0.101f)).isSolid()) return -0.101f;
        if (level.getBlockState(BlockPos.containing(x+0.101f, y, z - 0.101f)).isSolid()) return 0.101f;

        if (level.getBlockState(BlockPos.containing(x, y, z + 0.101f)).isSolid()) return -0.101f;
        if (level.getBlockState(BlockPos.containing(x, y, z - 0.101f)).isSolid()) return 0.101f;

        return 0f;
    }
}
