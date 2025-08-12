package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptations;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.common.data.adaptations.EnhancedStamina;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnEntityTick {

    private static int tick;

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {



            if (!player.level().isClientSide) {
                int newCD = player.getData(DataAttachments.ATTACK_COOLDOWN) - 1;
                player.setData(DataAttachments.ATTACK_COOLDOWN, newCD);
                PacketDistributor.sendToAllPlayers(new SyncAttackCooldown(player.getId(), newCD));

                int newKT = (player.onGround() || player.isInWater()) ? player.getData(DataAttachments.KNOCKDOWN_TIME) - 1 : player.getData(DataAttachments.KNOCKDOWN_TIME);
                player.setData(DataAttachments.KNOCKDOWN_TIME, newKT);
                PacketDistributor.sendToAllPlayers(new SyncKnockdownTime(player.getId(), newKT));

            }

            handleDeath(player);

            if (!player.getData(DataAttachments.DINO_DATA).isInitialized())
                initialize(player, player.getData(DataAttachments.DINO_DATA));



            tick++;
            if (tick > 10) {
                tick = 0;

                handleAttribute(player);

                if (!player.level().isClientSide())  {
                    PacketDistributor.sendToPlayersTrackingEntity(player, new SyncSelectedDinosaur(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur())); // sync selected dino to other clients

                    PacketDistributor.sendToAllPlayers(new SyncIsMale(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).isMale()));

                    syncSkinData(player);
                    updateRestingFactor(player);
                    handleGrowth(player);
                    handleRegenerationAndUpdateWeight(player);
                    handleWaterTick(player);
                    foodTick(player);
                    handleStaminaTick(player);
                    updatePairs(player);

                    if (player.getData(DataAttachments.PAIRING_STATE) > 0 && player.getData(DataAttachments.PAIRING_STATE) < 3) {
                        int newPairingState = player.getData(DataAttachments.PAIRING_STATE) + 1;

                        player.setData(DataAttachments.PAIRING_STATE, newPairingState);
                        PacketDistributor.sendToAllPlayers(new SyncPairingState(newPairingState, player.getId()));

                    }
                }
            }
        }
    }

    private static void foodTick(Player player) {

        float newFoodLevel = (Math.max(0f,  player.getData(DataAttachments.DINO_DATA).getFoodLevel()
                - Util.getDino(player).getStarvationPerSec() * 0.5f));


        player.getData(DataAttachments.DINO_DATA).setFoodLevel(newFoodLevel);
        PacketDistributor.sendToAllPlayers(new SyncFoodLevel(player.getId(), newFoodLevel));

    }

    private static void updateRestingFactor(Player player) {

        if (player.getData(DataAttachments.RESTING_STATE) == 2) {
            player.setData(DataAttachments.REST_FACTOR, player.getData(DataAttachments.REST_FACTOR) + 0.03f);
        } else {
            player.setData(DataAttachments.REST_FACTOR, 1f);
        }
    }

    private static void syncSkinData(Player player) {

        SkinData data = player.getData(DataAttachments.SKIN_DATA);

        PacketDistributor.sendToAllPlayers(new SyncSkinData(player.getId(), data.getEyesColor(),
                data.getMarkingsColor(), data.getBodyColor(), data.getFlankColor(),
                data.getBellyColor(), data.getMaleDisplayColor()));
    }


    private static void handleDeath(Player player) {
        if ((player.getData(DataAttachments.DINO_DATA).getBloodLevel() <= 0f ||
                player.getData(DataAttachments.DINO_DATA).getHealth() <= 0f) &&
                !player.level().isClientSide()) {

            DinosaurEntity corpse = Util.getBody(player);
            corpse.setBody(true);
            corpse.setPos(player.getX(), player.getY(), player.getZ());

            SkinData skinData = player.getData(DataAttachments.SKIN_DATA);

            corpse.setBodyColor(skinData.getBodyColor());
            corpse.setBellyColor(skinData.getBellyColor());
            corpse.setEyesColor(skinData.getEyesColor());
            corpse.setMarkingsColor(skinData.getMarkingsColor());
            corpse.setFlankColor(skinData.getFlankColor());
            corpse.setMaleDisplayColor(skinData.getMaleDisplayColor());

            corpse.setBodyGrowth(player.getData(DataAttachments.DINO_DATA).getGrowth());
            corpse.setBodyRot(player.getData(DataAttachments.PLAYER_ROTATION));
            corpse.playerId = player.getId();
            corpse.setOldPlayer(player.getId());
            corpse.setMale(player.getData(DataAttachments.DINO_DATA).isMale());

            player.level().addFreshEntity(corpse);


            player.getData(DataAttachments.DINO_DATA).fullReset();
            player.getData(DataAttachments.PAIRING_DATA).reset(true);
            PacketDistributor.sendToAllPlayers(new SyncFullReset(player.getId()));
        }
    }

    private static void handleGrowth(Player player) {
        float newGrowth = player.getData(DataAttachments.DINO_DATA).getGrowth()
                + Util.getDino(player).getGrowthPerMin() / 120f;
        newGrowth = Math.min(1f, newGrowth);

        if (player.getData(DataAttachments.DINO_DATA).isInitialized()) {
            player.getData(DataAttachments.DINO_DATA).setGrowth(newGrowth);
            PacketDistributor.sendToAllPlayers(new SyncGrowth(newGrowth, player.getId()));
            player.refreshDimensions();
        }
    }

    private static void handleRegenerationAndUpdateWeight(Player player) {

        float cubicGrowth = (float) Math.pow(player.getData(DataAttachments.DINO_DATA).getGrowth(), 3);
        float bleedRegen = 0.02f;

        if (player.getData(DataAttachments.DINO_DATA).isSprinting() && player.getData(DataAttachments.DINO_DATA).isMoving() ) {
            bleedRegen = Util.getDino(player).getBleedResistance() * 0.02f;
        }

        if (!player.getData(DataAttachments.DINO_DATA).isSprinting() && player.getData(DataAttachments.DINO_DATA).isMoving() ) {
            bleedRegen = Util.getDino(player).getBleedResistance() * 0.10f;
        }

        if (!player.getData(DataAttachments.DINO_DATA).isMoving() ) {
            bleedRegen = Util.getDino(player).getBleedResistance() * 0.25f;
        }

        if (player.getData(DataAttachments.RESTING_STATE) == 2) {
            bleedRegen = Util.getDino(player).getBleedResistance() * 0.7f;
            bleedRegen = (float) (bleedRegen * Math.pow(player.getData(DataAttachments.REST_FACTOR), 0.2d));
        }


        float newWeight = Mth.lerp(cubicGrowth, Util.getDino(player).getStartWeight(), Util.getDino(player).getWeight());

        float healthWeightRatio =  player.getData(DataAttachments.DINO_DATA).getHealth() /
                player.getData(DataAttachments.DINO_DATA).getWeight();

        float bloodWeightRatio =  player.getData(DataAttachments.DINO_DATA).getBloodLevel() /
                player.getData(DataAttachments.DINO_DATA).getWeight();

        float newHealth = (newWeight * healthWeightRatio);
        float newBloodLevel = (newWeight * bloodWeightRatio);
        float healthRegen = player.getData(DataAttachments.RESTING_STATE) == 2 ? Util.getDino(player).getHealthRegen() : 0;

        newHealth = newHealth + (healthRegen * newWeight * player.getData(DataAttachments.REST_FACTOR));

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
        PacketDistributor.sendToAllPlayers(new SyncWeight(player.getId(), newWeight));

    }


    private static void handleWaterTick(Player player) {
        float newWaterLevel = player.getData(DataAttachments.DINO_DATA).getWaterLevel();

        if (player.getData(DataAttachments.DINO_DATA).isDrinking()) {

            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            if (player.level().getBiome(BlockPos.containing(x, y, z)).is(BiomeTags.IS_OCEAN)) {
                Adaptation data = player.getData(DataAttachments.ADAPTATION_DATA).SALTWATER_TOLERANCE;
                float progress = data.getProgress() + 0.0025f;


                newWaterLevel = Math.min(1f, newWaterLevel + 0.0075f);
                data.setProgress(progress);
                PacketDistributor.sendToAllPlayers(new SyncAdaptation(Adaptations.SALTWATER_TOLERANCE, progress, data.getLevel(), player.getId(), data.isUpgraded()));

            } else {
                newWaterLevel = Math.min(1f, newWaterLevel + 0.015f);
            }

            if (newWaterLevel >= 0.999f) {
                player.getData(DataAttachments.DINO_DATA).setDrinking(false);
                PacketDistributor.sendToAllPlayers(new RequestDrinking(false, player.getId(), 0, 0));
            }
        } else {
            newWaterLevel = (Math.max(0f,  player.getData(DataAttachments.DINO_DATA).getWaterLevel()
                    - Util.getDino(player).getDehydrationPerSec() * 0.5f));
        }

        player.getData(DataAttachments.DINO_DATA).setWaterLevel(newWaterLevel);
        PacketDistributor.sendToAllPlayers(new SyncWaterLevel(player.getId(), newWaterLevel));
    }



    private static void initialize(Player player, DinoData data) {
        if (!player.level().isClientSide) {

            if (data.getSelectedDinosaur() != 0) {

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

    private static void handleAttribute(Player player) {
        ResourceLocation damageModifier = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "damageless");

        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (damage.getModifier(damageModifier) != null) {
            damage.removeModifier(damageModifier);
        }

        AttributeModifier modifier = new AttributeModifier(
                damageModifier,
                -999,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        damage.addPermanentModifier(modifier);
    }

    private static void handleStaminaTick(Player player) {

        EnhancedStamina enhancedStamina = player.getData(DataAttachments.ADAPTATION_DATA).ENHANCED_STAMINA;

        if (!player.getData(DataAttachments.DINO_DATA).isSprinting() || !player.getData(DataAttachments.DINO_DATA).isMoving()) {

            float additionalStam = Util.getStamRegen(player)
                    * (player.getData(DataAttachments.REST_FACTOR) * 0.5f + 0.5f);
            float newStam = Math.min(Util.getStaminaUpgraded(player),
                    player.getData(DataAttachments.DINO_DATA).getStamina() + additionalStam);

            if (newStam >= Util.getStaminaUpgraded(player) * 0.5f) {

                player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
                PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));
            } else if (newStam >= Util.getStaminaUpgraded(player) * 0.25 && newStam < Util.getStaminaUpgraded(player) * 0.5 ) {
                if (!player.getData(DataAttachments.DINO_DATA).isMoving()) {
                    belowHalfStamRegenTick(player, newStam, enhancedStamina, additionalStam);
                }
            } else {
                if (player.getData(DataAttachments.RESTING_STATE) == 2) {
                    belowHalfStamRegenTick(player, newStam, enhancedStamina, additionalStam);
                }
            }
        } else {

            if (player.getData(DataAttachments.DINO_DATA).isMoving()) {

                float newStam = Math.max(0, player.getData(DataAttachments.DINO_DATA).getStamina()
                        - 5f);

                player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
                PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));
            }
        }
    }

    private static void belowHalfStamRegenTick(Player player, float newStam, EnhancedStamina enhancedStamina, float additionalStam) {
        player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
        PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));

        float newProgress = enhancedStamina.getProgress() + additionalStam / (2 * Util.getStaminaUpgraded(player));
        player.getData(DataAttachments.ADAPTATION_DATA).ENHANCED_STAMINA.setProgress(newProgress);

        PacketDistributor.sendToAllPlayers(new SyncAdaptation(Adaptations.ENHANCED_STAMINA, newProgress,
                player.getData(DataAttachments.ADAPTATION_DATA).ENHANCED_STAMINA.getLevel(), player.getId(),
                player.getData(DataAttachments.ADAPTATION_DATA).ENHANCED_STAMINA.isUpgraded()));
    }

    private static void updatePairs(Player player) {
        ServerLevel level = (ServerLevel) player.level();

        LifeData data = LifeData.get(level);
        data.updateFor(player);

        UUID mateUUID = player.getData(DataAttachments.PAIRING_DATA).getMateUUID();
        if (mateUUID.equals(PairData.NO_MATE)) return;


        UUID mateLifeUUID = player.getData(DataAttachments.PAIRING_DATA).getMateLifeUUID();
        if (mateLifeUUID.equals(PairData.NO_MATE)) return;


        UUID actualMateLifeUUID = data.getLifeOf(mateUUID);
        if (actualMateLifeUUID == null) return;


        if (!mateLifeUUID.equals(actualMateLifeUUID)) {
            player.getData(DataAttachments.PAIRING_DATA).reset(false);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPairingReset(player.getId()));
            player.sendSystemMessage(Component.literal("Your mate died so you have been unpaired."));
        }
    }
}
