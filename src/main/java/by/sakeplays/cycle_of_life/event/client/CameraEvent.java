package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;


@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CameraEvent {


    private static float yaw = 0;
    private static float pitch = 0;
    private static float roll = 0;

    public static float rawRoll = 0;
    public static float rawPitch = 0;
    public static float rawYaw = 0;

    private static int lastProcessedTick = 0;

    @SubscribeEvent
    public static void camera(ViewportEvent.ComputeCameraAngles event) {

        Player player = Minecraft.getInstance().player;
        if (player == null) return;


        if (false) return;

        if (player.getData(DataAttachments.DINO_DATA).isFlying()) {

            if (lastProcessedTick != player.tickCount) {
                lastProcessedTick = player.tickCount;

                rawYaw = player.getData(DataAttachments.PLAYER_ROTATION) * Mth.RAD_TO_DEG;
            }

            player.displayClientMessage(Component.literal("X " + Minecraft.getInstance().mouseHandler.xpos() + ", Y: " + Minecraft.getInstance().mouseHandler.ypos()), true);


            yaw = smooth(yaw, rawYaw, 0.15f);
            pitch = smooth(pitch, rawPitch, 0.1f);
            roll = smooth(roll, rawRoll, 0.1f);

        } else {
            yaw = smooth(yaw, 0, 0.15f);
            pitch = smooth(pitch, 0, 0.1f);
            roll = smooth(roll, 0, 0.1f);
        }

        event.setYaw(event.getYaw() + yaw);
        event.setRoll(event.getRoll() + roll);
        event.setPitch(event.getPitch() + pitch);
    }

    private static float smooth(float current, float target, float smoothing) {
        float factor = 1.0f - (float)Math.pow(1.0f - smoothing, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());

        return current + Mth.wrapDegrees(target - current) * factor;
    }
}
