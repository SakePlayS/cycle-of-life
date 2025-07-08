package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    public static void onPlayerLogIn(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) return;

            if (!player.getData(DataAttachments.HITBOXES_INITIALIZED)) {
                player.setData(DataAttachments.HITBOXES_INITIALIZED, true);


                HitboxEntity headHitbox = new HitboxEntity(COLEntities.HITBOX.get(), player.level());
                headHitbox.setPlayerId(player.getId());
                headHitbox.setHitboxType("HEAD");

                player.level().addFreshEntity(headHitbox);


                HitboxEntity body1 = new HitboxEntity(COLEntities.HITBOX.get(), player.level());
                body1.setPlayerId(player.getId());
                body1.setHitboxType("BODY1");

                player.level().addFreshEntity(body1);


                HitboxEntity body2 = new HitboxEntity(COLEntities.HITBOX.get(), player.level());
                body2.setPlayerId(player.getId());
                body2.setHitboxType("BODY2");

                player.level().addFreshEntity(body2);



                HitboxEntity tail2 = new HitboxEntity(COLEntities.HITBOX.get(), player.level());
                tail2.setPlayerId(player.getId());
                tail2.setHitboxType("TAIL1");

                player.level().addFreshEntity(tail2);


                HitboxEntity tail3 = new HitboxEntity(COLEntities.HITBOX.get(), player.level());
                tail3.setPlayerId(player.getId());
                tail3.setHitboxType("TAIl2");

                player.level().addFreshEntity(tail3);
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.getData(DataAttachments.HITBOXES_INITIALIZED)) {
                player.setData(DataAttachments.HITBOXES_INITIALIZED, false);
            }
        }
    }
}
