package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.DataAttachments;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import by.sakeplays.cycle_of_life.network.to_client.SyncTurnDegree2C;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BlockBreakEvent {

    private static int tick;

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {
            tick++;
            if (tick > 10) {
                tick = 0;
                if (!player.level().isClientSide())  {
                    PacketDistributor.sendToPlayersTrackingEntity(player, new SyncSelectedDinosaur(player.getId(),  // sync selected dino to other players
                            player.getData(DataAttachments.SELECTED_DINOSAUR).getValue()));
                }
            }

            if (!player.level().isClientSide())  {
                PacketDistributor.sendToPlayersTrackingEntity(player, new SyncTurnDegree2C(player.getId(),   // sync turn degree to other players
                        player.getData(DataAttachments.TURN_DEGREE)));
            }

        }

    }


    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player loggedInPlayer = event.getEntity();
        List<ServerPlayer> playerList;

        if (loggedInPlayer.getServer() == null) {

            return;
        }

        playerList = loggedInPlayer.getServer().getPlayerList().getPlayers();

        for (ServerPlayer serverPlayer : playerList) {
            if (serverPlayer != loggedInPlayer) {
                PacketDistributor.sendToPlayer((ServerPlayer) loggedInPlayer, new SyncSelectedDinosaur(serverPlayer.getId(),
                        serverPlayer.getData(DataAttachments.SELECTED_DINOSAUR).getValue()));
            }
        }

        PacketDistributor.sendToPlayer((ServerPlayer) loggedInPlayer, new SyncSelectedDinosaur(loggedInPlayer.getId(),
                loggedInPlayer.getData(DataAttachments.SELECTED_DINOSAUR).getValue()));
    }


}
