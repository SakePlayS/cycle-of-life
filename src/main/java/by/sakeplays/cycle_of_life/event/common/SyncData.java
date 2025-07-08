package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
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
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SyncData {

    private static int tick;

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {

            if ((player.getData(DataAttachments.DINO_DATA).getBloodLevel() <= 0f ||
                    player.getData(DataAttachments.DINO_DATA).getHealth() <= 0f) &&
                    !player.level().isClientSide()) {

                player.getData(DataAttachments.DINO_DATA).fullReset();
                PacketDistributor.sendToAllPlayers(new SyncFullReset(player.getId()));
            }

            if (! player.getData(DataAttachments.DINO_DATA).isInitialized()) {
                initialize(player,  player.getData(DataAttachments.DINO_DATA));
            }


            tick++;
            if (tick > 10) {
                tick = 0;
                if (!player.level().isClientSide())  {
                    PacketDistributor.sendToPlayersTrackingEntity(player, new SyncSelectedDinosaur(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur())); // sync selected dino to other clients

                    PacketDistributor.sendToAllPlayers(new SyncIsMale(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).isMale()));

                    // update resting factor

                    if (player.getData(DataAttachments.RESTING_STATE) == 2) {
                        player.setData(DataAttachments.REST_FACTOR, player.getData(DataAttachments.REST_FACTOR) + 0.03f);
                    } else {
                        player.setData(DataAttachments.REST_FACTOR, 1f);
                    }

                    // Growth tick

                    float newGrowth =  player.getData(DataAttachments.DINO_DATA).getGrowth()
                            + Util.getDino(player).getGrowthPerMin() / 120f;
                    newGrowth = Math.min(1f, newGrowth);

                    player.getData(DataAttachments.DINO_DATA).setGrowth(newGrowth);
                    PacketDistributor.sendToAllPlayers(new SyncGrowth(newGrowth, player.getId()));

                    // tick weight, health and blood level

                    float bleedRegen = Util.getDino(player).getBleedResistance();

                    if (player.getData(DataAttachments.DINO_DATA).isSprinting() && player.getData(DataAttachments.DINO_DATA).isMoving() ) {
                        bleedRegen = bleedRegen * 0.02f;
                    }

                    if (!player.getData(DataAttachments.DINO_DATA).isSprinting() && player.getData(DataAttachments.DINO_DATA).isMoving() ) {
                        bleedRegen = bleedRegen * 0.10f;
                    }

                    if (!player.getData(DataAttachments.DINO_DATA).isMoving() ) {
                        bleedRegen = bleedRegen * 0.25f;
                    }

                    if (player.getData(DataAttachments.RESTING_STATE) == 2) {
                        bleedRegen = Util.getDino(player).getBleedResistance();
                        bleedRegen = (float) (bleedRegen * Math.pow(player.getData(DataAttachments.REST_FACTOR), 0.2d));
                    }


                    float newWeight = Mth.lerp(newGrowth, Util.getDino(player).getStartWeight(), Util.getDino(player).getWeight());

                    float healthWeightRatio =  player.getData(DataAttachments.DINO_DATA).getHealth() /
                            player.getData(DataAttachments.DINO_DATA).getWeight();

                    float bloodWeightRatio =  player.getData(DataAttachments.DINO_DATA).getBloodLevel() /
                            player.getData(DataAttachments.DINO_DATA).getWeight();

                    float newHealth = (newWeight * healthWeightRatio);
                    float newBloodLevel = (newWeight * bloodWeightRatio);

                    newHealth = newHealth + (Util.getDino(player).getHealthRegen() * newWeight * player.getData(DataAttachments.REST_FACTOR));

                    if (!(player.getData(DataAttachments.DINO_DATA).getBleed() > 0)) {
                        newBloodLevel = newBloodLevel + (Util.getDino(player).getHealthRegen() * newWeight);
                    } else {
                        newBloodLevel = newBloodLevel - (player.getData(DataAttachments.DINO_DATA).getBleed());
                        float newBleed = player.getData(DataAttachments.DINO_DATA).getBleed() - bleedRegen;

                        player.getData(DataAttachments.DINO_DATA).setBleed(newBleed);
                        PacketDistributor.sendToAllPlayers(new SyncBleed(player.getId(), newBleed));
                    }

                    if (newHealth > newWeight) newHealth = newWeight;
                    if (newBloodLevel > newWeight) newBloodLevel = newWeight;

                    player.getData(DataAttachments.DINO_DATA).setHealth(newHealth);
                    PacketDistributor.sendToAllPlayers(new SyncHealth(player.getId(), newHealth));

                    player.getData(DataAttachments.DINO_DATA).setBloodLevel(newBloodLevel);
                    PacketDistributor.sendToAllPlayers(new SyncBloodLevel(player.getId(), newBloodLevel));

                    player.getData(DataAttachments.DINO_DATA).setWeight(newWeight);
                    PacketDistributor.sendToAllPlayers(new SyncWeight(player.getId(), newWeight));  // sync weight to other clients


                    // tick food and water

                    float newWaterLevel = (Math.max(0f,  player.getData(DataAttachments.DINO_DATA).getWaterLevel()
                            - Util.getDino(player).getDehydrationPerSec() * 0.5f));
                    float newFoodLevel = (Math.max(0f,  player.getData(DataAttachments.DINO_DATA).getFoodLevel()
                            - Util.getDino(player).getStarvationPerSec() * 0.5f));

                    player.getData(DataAttachments.DINO_DATA).setWaterLevel(newWaterLevel);
                    PacketDistributor.sendToAllPlayers(new SyncWaterLevel(player.getId(), newWaterLevel));

                    player.getData(DataAttachments.DINO_DATA).setFoodLevel(newFoodLevel);
                    PacketDistributor.sendToAllPlayers(new SyncFoodLevel(player.getId(), newFoodLevel));


                    // Tick stamina

                    if (! player.getData(DataAttachments.DINO_DATA).isSprinting()) {

                        float newStam = Math.min(Util.getDino(player).getStaminaPool(),
                                player.getData(DataAttachments.DINO_DATA).getStamina() + Util.getStamRegen(player)
                                        * player.getData(DataAttachments.REST_FACTOR));

                        player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
                        PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));
                    } else {
                        float newStam = Math.max(0,  player.getData(DataAttachments.DINO_DATA).getStamina()
                                - 3.5f);

                        player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
                        PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));
                    }
                }
            }

            float turnDegree = player.getData(DataAttachments.PLAYER_TURN);

            if (!player.level().isClientSide())  {

                Util.recordYHistory(player, (float) player.getY());
                PacketDistributor.sendToAllPlayers(new SyncYHistory(player.getId(),  (float) player.getY()));
            } else {

                Util.recordTurnHistory(player, turnDegree);
                PacketDistributor.sendToServer(new SyncTurnHistory(player.getId(), turnDegree));

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

    private static void initialize(Player player, DinoData data) {
        if (!player.level().isClientSide) {

            if (data.getSelectedDinosaur() != 0) {

                if (Math.random() < 0.5) {
                    data.setMale(false);
                    PacketDistributor.sendToAllPlayers(new SyncIsMale(player.getId(), false));
                } else {
                    data.setMale(true);
                    PacketDistributor.sendToAllPlayers(new SyncIsMale(player.getId(), true));
                }

                data.setWeight(Util.getDino(player).getStartWeight());
                PacketDistributor.sendToAllPlayers(new SyncWeight(player.getId(), Util.getDino(player).getStartWeight()));

                data.setHealth(Util.getDino(player).getWeight());
                PacketDistributor.sendToAllPlayers(new SyncHealth(player.getId(), (Util.getDino(player).getStartWeight())));

                data.setBloodLevel(Util.getDino(player).getWeight());
                PacketDistributor.sendToAllPlayers(new SyncBloodLevel(player.getId(), (Util.getDino(player).getStartWeight())));

                data.setStamina(Util.getDino(player).getStaminaPool());
                PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), Util.getDino(player).getStaminaPool()));

                data.setInitialized(true);
                PacketDistributor.sendToAllPlayers(new SyncInitialized(player.getId(), true));
            }
        }
    }
}
