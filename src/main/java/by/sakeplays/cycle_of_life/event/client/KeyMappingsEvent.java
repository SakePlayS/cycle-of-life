package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class KeyMappingsEvent {
    public static final KeyMapping FORWARD_MAPPING =
            new KeyMapping("key.cycle_of_life.forward", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W,
                    "key.categories.cycle_of_life.movement");

    public static final KeyMapping BACKWARDS_MAPPING =
            new KeyMapping("key.cycle_of_life.backwards", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_S,
                    "key.categories.cycle_of_life.movement");

    public static final KeyMapping LEFT_MAPPING =
            new KeyMapping("key.cycle_of_life.left", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_A,
                    "key.categories.cycle_of_life.movement");

    public static final KeyMapping RIGHT_MAPPING =
            new KeyMapping("key.cycle_of_life.right", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_D,
                    "key.categories.cycle_of_life.movement");

    public static final KeyMapping SPRINT_MAPPING =
            new KeyMapping("key.cycle_of_life.growth", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL,
                    "key.categories.cycle_of_life.movement");

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(FORWARD_MAPPING);
        event.register(BACKWARDS_MAPPING);
        event.register(LEFT_MAPPING);
        event.register(RIGHT_MAPPING);
        event.register(SPRINT_MAPPING);
    }
}

