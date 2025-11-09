package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationType;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncFlightState;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncIsPaired;
import by.sakeplays.cycle_of_life.network.to_client.SyncMateName;
import by.sakeplays.cycle_of_life.network.to_client.SyncMateUUID;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SyncHandler {


    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();

        if (player.level().isClientSide) {
            ClientNestData.ownNest = null;
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide) {

            player.setData(DataAttachments.REST_FACTOR, 1f);

            AdaptationData data = player.getData(DataAttachments.ADAPTATION_DATA);

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncAdaptation(AdaptationType.SALTWATER_TOLERANCE, data.getAdaptation(AdaptationType.SALTWATER_TOLERANCE).getProgress(),
                            data.getAdaptation(AdaptationType.SALTWATER_TOLERANCE).getLevel(), player.getId(), data.getAdaptation(AdaptationType.SALTWATER_TOLERANCE).isUpgraded()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncAdaptation(AdaptationType.ENHANCED_STAMINA, data.getAdaptation(AdaptationType.ENHANCED_STAMINA).getProgress(),
                            data.getAdaptation(AdaptationType.ENHANCED_STAMINA).getLevel(), player.getId(), data.getAdaptation(AdaptationType.ENHANCED_STAMINA).isUpgraded()));

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

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncStoredEggs(player.getData(DataAttachments.PAIRING_DATA).getStoredEggs(), player.getId()));

            PacketDistributor.sendToAllPlayers(
                    new SyncBuildMode(player.getId(), player.getData(DataAttachments.DINO_DATA).isInHumanMode()));

            PacketDistributor.sendToAllPlayers(
                    new SyncFlightState(player.getData(DataAttachments.DINO_DATA).isFlying(), player.getId()));

            player.setData(DataAttachments.VANILLA_IFRAME_COMPAT_UNTILL, 100);

        }
    }

    private static final Set<UUID> syncedPlayers = new HashSet<>();


    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {

            if (!syncedPlayers.contains(player.getUUID())) {
                MinecraftServer minecraftServer = player.getServer();
                NestData nestData = NestData.get(minecraftServer);
                PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

                Nest nest = nestData.getNestByPlayer(player);

                if (nest == null) {
                    syncedPlayers.add(player.getUUID());
                    return;
                }

                PacketDistributor.sendToPlayer(player, new SyncOwnNest(nest));
                syncedPlayers.add(player.getUUID());
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (!level.isClientSide) {
            MinecraftServer minecraftServer = level.getServer();

            NestData.get(minecraftServer).update(minecraftServer);
        }
    }
}
