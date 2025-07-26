package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CreateHitboxes {

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {
            float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();

            AABB headHitbox =  createHitbox(player.getData(DataAttachments.HITBOX_DATA).getHeadHitboxPos(), 0.5f * growth, 0.35f * growth);

        }
    }

    private static AABB createHitbox(Position center, float width, float height) {
        return new AABB(
                center.x() - width/2, center.y() - height/2, center.z() - width/2,
                center.x() + width/2, center.y() + height/2, center.z() + width/2
        );
    }
}
