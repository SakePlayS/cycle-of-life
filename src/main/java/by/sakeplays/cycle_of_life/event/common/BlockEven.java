package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BlockEven {

    @SubscribeEvent
    public static void block(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().getData(DataAttachments.DINO_DATA).isInBuildMode()) return;
        event.setCanceled(true);
    }

}
