package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptations;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncIsPaired;
import by.sakeplays.cycle_of_life.network.to_client.SyncMateName;
import by.sakeplays.cycle_of_life.network.to_client.SyncMateUUID;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerLoggedIn {

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide) {

            ServerLevel serverLevel = (ServerLevel) player.level();

            player.setData(DataAttachments.REST_FACTOR, 1f);

            AdaptationData data = player.getData(DataAttachments.ADAPTATION_DATA);
            PairData pairData = player.getData(DataAttachments.PAIRING_DATA);
            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncAdaptation(Adaptations.SALTWATER_TOLERANCE, data.SALTWATER_TOLERANCE.getProgress(),
                            data.SALTWATER_TOLERANCE.getLevel(), player.getId(), data.SALTWATER_TOLERANCE.isUpgraded()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncAdaptation(Adaptations.ENHANCED_STAMINA, data.ENHANCED_STAMINA.getProgress(),
                            data.ENHANCED_STAMINA.getLevel(), player.getId(), data.ENHANCED_STAMINA.isUpgraded()));

            PacketDistributor.sendToAllPlayers(
                    new SyncSelectedDinosaur(player.getId(), player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncStamina(player.getId(), player.getData(DataAttachments.DINO_DATA).getStamina()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncMateUUID(player.getData(DataAttachments.PAIRING_DATA).getMateUUID().toString(), player.getId()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncNestUUID(player.getData(DataAttachments.PAIRING_DATA).getNestUUID().toString(), player.getId()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncMateName(player.getData(DataAttachments.PAIRING_DATA).getMateName(), player.getId()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncIsPaired(player.getData(DataAttachments.PAIRING_DATA).isPaired(), player.getId()));

            NestData nestData = NestData.get(serverLevel);

            if (pairData.isPaired()) {

                Nest nest = nestData.getNestByID(dinoData.isMale() ? player.getUUID() : pairData.getMateUUID());

                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncFullNestData(nest));
            }

        }
    }
}
