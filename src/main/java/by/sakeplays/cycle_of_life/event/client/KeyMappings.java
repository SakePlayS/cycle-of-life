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
public class KeyMappings {
    public static final KeyMapping FORWARD_MAPPING =
            new KeyMapping("key.cycle_of_life.forward", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W,
                    "key.categories.cycle_of_life");

    public static final KeyMapping LEFT_MAPPING =
            new KeyMapping("key.cycle_of_life.left", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_A,
                    "key.categories.cycle_of_life");

    public static final KeyMapping RIGHT_MAPPING =
            new KeyMapping("key.cycle_of_life.right", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_D,
                    "key.categories.cycle_of_life");

    public static final KeyMapping BACKWARD_MAPPING =
            new KeyMapping("key.cycle_of_life.backward", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_S,
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

    public static final KeyMapping MAIN_ATTACK_MAPPING_2 =
            new KeyMapping("key.cycle_of_life.main_attack_2", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    "key.categories.cycle_of_life");

    public static final KeyMapping REST_MAPPING =
            new KeyMapping("key.cycle_of_life.rest", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H,
                    "key.categories.cycle_of_life");

    public static final KeyMapping PAIR_MAPPING =
            new KeyMapping("key.cycle_of_life.pair", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P,
                    "key.categories.cycle_of_life");

    public static final KeyMapping PLACE_NEST_MAPPING =
            new KeyMapping("key.cycle_of_life.place_nest", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N,
                    "key.categories.cycle_of_life");

    public static final KeyMapping GRAB_MAPPING =
            new KeyMapping("key.cycle_of_life.grab", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G,
                    "key.categories.cycle_of_life");

    public static final KeyMapping DRINK_MAPPING =
            new KeyMapping("key.cycle_of_life.drink", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E,
                    "key.categories.cycle_of_life");

    public static final KeyMapping EAT_MAPPING =
            new KeyMapping("key.cycle_of_life.eat", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E,
                    "key.categories.cycle_of_life");

    public static final KeyMapping CHARACTER_MAPPING =
            new KeyMapping("key.cycle_of_life.character_info", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_TAB,
                    "key.categories.cycle_of_life");

    public static final KeyMapping TAKEOFF_MAPPING =
            new KeyMapping("key.cycle_of_life.takeoff", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE,
                    "key.categories.cycle_of_life");

    public static final KeyMapping ASCEND =
            new KeyMapping("key.cycle_of_life.ascend", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE,
                    "key.categories.cycle_of_life");

    public static final KeyMapping DESCEND =
            new KeyMapping("key.cycle_of_life.descend", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT,
                    "key.categories.cycle_of_life");

    public static final KeyMapping AIRBRAKE =
            new KeyMapping("key.cycle_of_life.airbrake", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_S,
                    "key.categories.cycle_of_life");

    public static final KeyMapping TOGGLE_CAMERA_MODE =
            new KeyMapping("key.cycle_of_life.toggle_camera_mode", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C,
                    "key.categories.cycle_of_life");

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(FORWARD_MAPPING);
        event.register(DIRECTIONAL_ATTACK);
        event.register(LEFT_MAPPING);
        event.register(RIGHT_MAPPING);
        event.register(SPRINT_MAPPING);
        event.register(MAIN_ATTACK_MAPPING);
        event.register(MAIN_ATTACK_MAPPING_2);
        event.register(REST_MAPPING);
        event.register(PAIR_MAPPING);
        event.register(DRINK_MAPPING);
        event.register(GRAB_MAPPING);
        event.register(CHARACTER_MAPPING);
        event.register(BACKWARD_MAPPING);
        event.register(PLACE_NEST_MAPPING);
        event.register(TAKEOFF_MAPPING);
        event.register(ASCEND);
        event.register(DESCEND);
        event.register(AIRBRAKE);
        event.register(EAT_MAPPING);
        event.register(TOGGLE_CAMERA_MODE);

    }

}

