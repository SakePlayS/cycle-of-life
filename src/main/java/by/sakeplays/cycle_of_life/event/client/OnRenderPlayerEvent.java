package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.screen.DinoSelectionScreen;
import by.sakeplays.cycle_of_life.common.DataAttachments;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.util.DinosaursList;
import com.mojang.blaze3d.platform.InputConstants;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class OnRenderPlayerEvent {

    private static final Map<Integer, DinosaurEntity> PLAYER_DINOS = new ConcurrentHashMap<>();


    // Suppress the  player model rendering and replace it
    // with a dinosaur model.

    public static DinosaurEntity getOrCreateDino(final Player player) {

        int key = player.getId();

        if (PLAYER_DINOS.containsKey(key)) {
            return PLAYER_DINOS.get(key);
        }

        player.sendSystemMessage(Component.literal("added"));

        DinosaurEntity entity = getDinoToRender(player);

        entity.playerId = key;
        PLAYER_DINOS.put(player.getId(), entity);

        return PLAYER_DINOS.get(player.getId());
    }

    @SubscribeEvent
    private static void onRender(RenderPlayerEvent.Pre event) {

        event.setCanceled(true);

        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        if (player.getData(DataAttachments.SELECTED_DINOSAUR).getValue() == 0) {
            return;
        }

        Minecraft instance = Minecraft.getInstance();
        DinosaurEntity dinosaurEntity = getOrCreateDino(player);

        if (!instance.options.getCameraType().isFirstPerson() || player != instance.player) {
            instance.getEntityRenderDispatcher().getRenderer(dinosaurEntity).render(dinosaurEntity, player.getViewYRot(event.getPartialTick()),
                    event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        }
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {

    }

    @SubscribeEvent
    public static void frameRender(RenderFrameEvent.Pre event) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        Minecraft instance = Minecraft.getInstance();

        if (instance.screen == null && player != null) {
            if (player.getData(DataAttachments.SELECTED_DINOSAUR).getValue() == 0) {
                instance.setScreen(new DinoSelectionScreen(Component.literal("Select the dino")));
            }
        }

        instance.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        instance.options.fov().set(85);
        instance.options.fovEffectScale().set(0.25d);

        instance.options.keyLeft.setKey(InputConstants.UNKNOWN);
        instance.options.keyRight.setKey(InputConstants.UNKNOWN);
        instance.options.keyUp.setKey(InputConstants.UNKNOWN);
        instance.options.keyDown.setKey(InputConstants.UNKNOWN);

    }

    public static void key() {

    }



    private static DinosaurEntity getDinoToRender(Player player) {
        int selectedDino = player.getData(DataAttachments.SELECTED_DINOSAUR).getValue();

        if (selectedDino == DinosaursList.PACHYCEPHALOSAURUS.getID()) return COLEntities.PACHYCEPHALOSAURUS.get().create(player.level());
        return COLEntities.DEINONYCHUS.get().create(player.level());

    }

}
