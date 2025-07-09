package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncTurnHistory;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncYHistory;
import by.sakeplays.cycle_of_life.network.to_server.SyncHitboxes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientTick {

    @SubscribeEvent
    public static void syncHitboxes(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Position head = player.getData(DataAttachments.HITBOX_DATA).getHeadHitboxPos();
        Position body1 = player.getData(DataAttachments.HITBOX_DATA).getBody1Pos();
        Position body2 = player.getData(DataAttachments.HITBOX_DATA).getBody2Pos();
        Position tail1 = player.getData(DataAttachments.HITBOX_DATA).getTail1Pos();
        Position tail2 = player.getData(DataAttachments.HITBOX_DATA).getTail2Pos();

        PacketDistributor.sendToServer(new SyncHitboxes(
                head.x(), head.y(), head.z(),
                body1.x(), body1.y(), body1.z(),
                body2.x(), body2.y(), body2.z(),
                tail1.x(), tail1.y(), tail1.z(),
                tail2.x(), tail2.y(), tail2.z()));

    }

    @SubscribeEvent
    public static void recordHistory(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        float turnDegree = player.getData(DataAttachments.PLAYER_TURN);

        Util.recordYHistory(player, (float) player.getY());
        PacketDistributor.sendToServer(new SyncYHistory(player.getId(),  (float) player.getY()));

        Util.recordTurnHistory(player, turnDegree);
        PacketDistributor.sendToServer(new SyncTurnHistory(player.getId(),  turnDegree));
    }
}
