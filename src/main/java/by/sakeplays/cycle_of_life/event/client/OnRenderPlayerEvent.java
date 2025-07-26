package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.screen.DinoSelectionScreen;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class OnRenderPlayerEvent {

    public static final Map<Integer, DinosaurEntity> PLAYER_DINOS = new ConcurrentHashMap<>();


    // Suppress the  player model rendering and replace it
    // with a dinosaur model.

    public static DinosaurEntity getOrCreateDino(final Player player) {

        int key = player.getId();

        if (PLAYER_DINOS.containsKey(key)) {
            return PLAYER_DINOS.get(key);
        }


        DinosaurEntity entity = getDinoToRender(player);

        entity.playerId = key;
        PLAYER_DINOS.put(player.getId(), entity);

        return PLAYER_DINOS.get(player.getId());
    }


    private static DinosaurEntity getDinoToRender(Player player) {
        int selectedDino = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (selectedDino == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return COLEntities.PACHYCEPHALOSAURUS.get().create(player.level());
        return COLEntities.DEINONYCHUS.get().create(player.level());

    }


    @SubscribeEvent
    private static void onRender(RenderPlayerEvent.Pre event) {

        event.setCanceled(true);

        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) {
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
    public static void frameRender(RenderFrameEvent.Pre event) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        Minecraft instance = Minecraft.getInstance();

        if (instance.screen == null && player != null) {
            if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) {
                instance.setScreen(new DinoSelectionScreen(Component.literal("Select the dino")));
            }
        }

        instance.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        instance.options.fov().set(85);
        instance.options.fovEffectScale().set(0.25d);
        instance.options.bobView().set(false);
        instance.options.entityShadows().set(false);

        instance.options.keyLeft.setKey(InputConstants.UNKNOWN);
        instance.options.keyRight.setKey(InputConstants.UNKNOWN);
        instance.options.keyUp.setKey(InputConstants.UNKNOWN);
        instance.options.keyDown.setKey(InputConstants.UNKNOWN);
        instance.options.keySprint.setKey(InputConstants.UNKNOWN);
        instance.options.keyInventory.setKey(InputConstants.UNKNOWN);
        instance.options.keySocialInteractions.setKey(InputConstants.UNKNOWN);
        instance.options.keyAttack.setKey(InputConstants.UNKNOWN);
        instance.options.keyUse.setKey(InputConstants.UNKNOWN);

    }
}
