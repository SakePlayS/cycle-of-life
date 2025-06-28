package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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

        DinosaurEntity entity = COLEntities.PACHYCEPHALOSAURUS.get().create(player.level());
        entity.playerId = key;
        PLAYER_DINOS.put(player.getId(), entity);

        return PLAYER_DINOS.get(player.getId());
    }

    @SubscribeEvent
    private static void onRender(RenderPlayerEvent.Pre event) {


        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        Minecraft instance = Minecraft.getInstance();
        DinosaurEntity dinosaurEntity = getOrCreateDino(player);



        if (!instance.options.getCameraType().isFirstPerson() || player != instance.player) {
            instance.getEntityRenderDispatcher().getRenderer(dinosaurEntity).render(dinosaurEntity, player.getViewYRot(event.getPartialTick()),
                    event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        }

        event.setCanceled(true);


    }

}
