package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.HitboxData;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CreatePlayerHitboxes {

    @SubscribeEvent
    public static void createHitboxes(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) {

            Player player = event.getEntity();

            if (player.tickCount < 20) return;

            if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) return;

            if (player.tickCount % 20 == 0) {

                if (!hitboxesExist(player)) {
                    player.setData(DataAttachments.HITBOXES_INITIALIZED, false);
                    CycleOfLife.LOGGER.warn("Hitboxes missing for " + player.getName().getString() + ". Reinitializing.");
                }
            }

            if (!player.getData(DataAttachments.HITBOXES_INITIALIZED)) {

                ServerLevel level = (ServerLevel) player.level();

                HitboxData data = player.getData(DataAttachments.HITBOX_DATA);

                data.setHeadId(spawnHitbox(level, player, "HEAD"));
                data.setBody1Id(spawnHitbox(level, player, "BODY1"));
                data.setBody2Id(spawnHitbox(level, player, "BODY2"));
                data.setTail1Id(spawnHitbox(level, player, "TAIL1"));
                data.setTail2Id(spawnHitbox(level, player, "TAIL2"));

                // if for whatever reason hitboxes fail to spawn, try again.

                if (!hitboxesExist(player)) {
                    player.setData(DataAttachments.HITBOXES_INITIALIZED, false);
                    CycleOfLife.LOGGER.warn("Hitboxes failed to spawn for " + player.getName().getString() + ". Retrying.");
                } else {
                    player.setData(DataAttachments.HITBOXES_INITIALIZED, true);
                }
            }
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
