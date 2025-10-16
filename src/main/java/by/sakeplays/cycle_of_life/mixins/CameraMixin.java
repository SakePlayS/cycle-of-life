package by.sakeplays.cycle_of_life.mixins;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {


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

        double x = Mth.lerp((double)partialTick, entity.xo, entity.getX());
        double y = Mth.lerp((double)partialTick, entity.yo, entity.getY()) + Math.max(0.101d, (double)Mth.lerp(partialTick, accessor.accessorGetEyeHeightOld(), accessor.accessorGetEyeHeight()));
        double z = Mth.lerp((double)partialTick, entity.zo, entity.getZ());


        float xoff = xOffset(x, y, z, entity.level());
        float zoff = zOffset(x, y, z, entity.level());

        x = x + xoff;
        z = z + zoff;


        accessor.callSetPosition(x, y, z);

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
