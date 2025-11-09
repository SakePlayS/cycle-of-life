package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnHistory;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncYHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientTick {


    @SubscribeEvent
    public static void recordHistory(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getData(DataAttachments.DINO_DATA).isInHumanMode()) return;


        float turnDegree = player.getData(DataAttachments.PLAYER_ROTATION);

        Util.recordYHistory(player, (float) player.getY());
        PacketDistributor.sendToServer(new SyncYHistory(player.getId(),  (float) player.getY()));

        Util.recordTurnHistory(player, turnDegree);
        PacketDistributor.sendToServer(new SyncTurnHistory(player.getId(),  turnDegree));
    }
}
