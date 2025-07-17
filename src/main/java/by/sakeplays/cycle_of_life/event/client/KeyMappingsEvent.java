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
                    "key.categories.cycle_of_life");

    public static final KeyMapping LEFT_MAPPING =
            new KeyMapping("key.cycle_of_life.left", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_A,
                    "key.categories.cycle_of_life");

    public static final KeyMapping RIGHT_MAPPING =
            new KeyMapping("key.cycle_of_life.right", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_D,
                    "key.categories.cycle_of_life");

    public static final KeyMapping SPRINT_MAPPING =
            new KeyMapping("key.cycle_of_life.sprint", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL,
                    "key.categories.cycle_of_life");

    public static final KeyMapping DIRECTIONAL_ATTACK =
            new KeyMapping("key.cycle_of_life.directional_attack", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT,
                    "key.categories.cycle_of_life");

    public static final KeyMapping MAIN_ATTACK_MAPPING =
            new KeyMapping("key.cycle_of_life.main_attack", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    "key.categories.cycle_of_life");

    public static final KeyMapping REST_MAPPING =
            new KeyMapping("key.cycle_of_life.rest", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H,
                    "key.categories.cycle_of_life");

    public static final KeyMapping PAIR_MAPPING =
            new KeyMapping("key.cycle_of_life.pair", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P,
                    "key.categories.cycle_of_life");

    public static final KeyMapping EAT_MAPPING =
            new KeyMapping("key.cycle_of_life.eat", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E,
                    "key.categories.cycle_of_life");

    public static final KeyMapping CHARACTER_MAPPING =
            new KeyMapping("key.cycle_of_life.character_info", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_TAB,
                    "key.categories.cycle_of_life");

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(FORWARD_MAPPING);
        event.register(DIRECTIONAL_ATTACK);
        event.register(LEFT_MAPPING);
        event.register(RIGHT_MAPPING);
        event.register(SPRINT_MAPPING);
        event.register(MAIN_ATTACK_MAPPING);
        event.register(REST_MAPPING);
        event.register(PAIR_MAPPING);
        event.register(EAT_MAPPING);
        event.register(CHARACTER_MAPPING);

    }
}

