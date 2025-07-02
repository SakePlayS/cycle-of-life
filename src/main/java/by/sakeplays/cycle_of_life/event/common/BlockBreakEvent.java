package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncGrowth;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import by.sakeplays.cycle_of_life.network.to_client.SyncTurnDegree2C;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnHistory;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncYHistory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
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
                    PacketDistributor.sendToPlayersTrackingEntity(player, new SyncSelectedDinosaur(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur())); // sync selected dino to other clients

                    float newGrowth = player.getData(DataAttachments.DINO_DATA).getGrowth()
                            + Util.getDino(player).getGrowthPerMin() / 120f;

                    newGrowth = Math.min(1f, newGrowth);

                    player.getData(DataAttachments.DINO_DATA).setGrowth(newGrowth);
                    PacketDistributor.sendToAllPlayers(new SyncGrowth(newGrowth, player.getId()));  // sync growth to other clients

                }
            }

            float turnDegree = player.getData(DataAttachments.DINO_DATA).getTurnDegree();

            if (!player.level().isClientSide())  {

                PacketDistributor.sendToPlayersTrackingEntity(player, new SyncTurnDegree2C(player.getId(),   // sync turn degree to other players
                        turnDegree));



            } else {
                Util.recordTurnHistory(player, turnDegree);
                PacketDistributor.sendToServer(new SyncTurnHistory(player.getId(), turnDegree));

                Util.recordYHistory(player, (float) player.getY());
                PacketDistributor.sendToServer(new SyncYHistory(player.getId(), (float) player.getY()));
            }

        }

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
                        serverPlayer.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()));
            }
        }

        PacketDistributor.sendToPlayer((ServerPlayer) loggedInPlayer, new SyncSelectedDinosaur(loggedInPlayer.getId(),
                loggedInPlayer.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()));
    }


}
