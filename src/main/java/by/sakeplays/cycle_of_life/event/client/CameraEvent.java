package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class CameraEvent {

    @SubscribeEvent
    public static void onCamera(ViewportEvent.ComputeCameraAngles event) {


    }
}
