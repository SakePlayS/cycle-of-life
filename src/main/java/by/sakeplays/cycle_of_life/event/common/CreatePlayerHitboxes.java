package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CreatePlayerHitboxes {

    @SubscribeEvent
    public static void createHitboxes(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) {

        }
    }

    private static boolean hitboxesExist(Player player) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        double size = 10;

        AABB hitbox = new AABB(
                x - size, y - size, z - size,
                x + size, y + size, z + size
        );


        List<HitboxEntity> hitboxes = player.level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), hitbox, e -> e.getPlayer() == player);

        return !hitboxes.isEmpty();

    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.getData(DataAttachments.HITBOXES_INITIALIZED)) {
                player.setData(DataAttachments.HITBOXES_INITIALIZED, false);
            }
        }
    }


    private static int spawnHitbox(ServerLevel level, Player player, String type) {
        HitboxEntity hitbox = new HitboxEntity(COLEntities.HITBOX.get(), level);
        hitbox.setPlayerId(player.getId());
        hitbox.setHitboxType(type);
        hitbox.setPos(player.getOnPos().getCenter());
        level.addFreshEntity(hitbox);
        return hitbox.getId();
    }
}
