package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.screen.DinoSelectionScreen;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.ModEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.util.Util;
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

    // IMPORTANT: Don't create a new entity every tick. Instead, you should store it somewhere.

    public static final Map<Integer, DinosaurEntity> PLAYER_DINOS = new ConcurrentHashMap<>();


    // Add a dino entity to the map if it doesn't exist
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

    // Get the entity to render
    private static DinosaurEntity getDinoToRender(Player player) {
        Dinosaurs selectedDino = Util.getDino(player);

        return switch (selectedDino) {
            case DEINONYCHUS -> ModEntities.DEINONYCHUS.get().create(player.level());
            case PTERANODON -> ModEntities.PTERANODON.get().create(player.level());
            case PACHYCEPHALOSAURUS -> ModEntities.PACHYCEPHALOSAURUS.get().create(player.level());

            default -> ModEntities.DEINONYCHUS.get().create(player.level());
        };
    }


    @SubscribeEvent
    private static void onRender(RenderPlayerEvent.Pre event) {

        if (event.getEntity().getData(DataAttachments.DINO_DATA).isInBuildMode()) return;

        event.setCanceled(true);

        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

        if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) {
            PLAYER_DINOS.remove(player.getId());
            return;
        }

        if (PLAYER_DINOS.containsKey(player.getId())) {
            if (!Util.getDino(player).equals(PLAYER_DINOS.get(player.getId()).getDinosaurSpecies())) {
                PLAYER_DINOS.remove(player.getId());
            }
        }


        Minecraft instance = Minecraft.getInstance();
        DinosaurEntity dinosaurEntity = getOrCreateDino(player);

        instance.getEntityRenderDispatcher().getRenderer(dinosaurEntity).render(dinosaurEntity, player.getViewYRot(event.getPartialTick()),
                event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());

    }


    // TODO: Move this stuff to somewhere else

    @SubscribeEvent
    public static void frameRender(RenderFrameEvent.Pre event) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        Minecraft instance = Minecraft.getInstance();

        if (player == null || player.getData(DataAttachments.DINO_DATA).isInBuildMode()) return;


        if (instance.screen == null) {
            if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) {
                instance.setScreen(new DinoSelectionScreen(Component.literal("Select the dino")));
            }
        }

        instance.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        instance.options.fov().set(85);
        instance.options.fovEffectScale().set(0.25d);
        instance.options.bobView().set(false);
        instance.options.entityShadows().set(false);
    }


}
