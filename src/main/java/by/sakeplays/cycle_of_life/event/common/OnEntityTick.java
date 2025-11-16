package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationType;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class OnEntityTick {


    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {

            if (!player.level().isClientSide && player.tickCount % 10 == 0) {
                PacketDistributor.sendToAllPlayers(new SyncBuildMode(player.getId(),
                        player.getData(DataAttachments.DINO_DATA).isInHumanMode()));
            }

            if (player.getData(DataAttachments.DINO_DATA).isInHumanMode()) {
                if (!player.getData(DataAttachments.DINO_DATA).isBuildModeUpdated()) player.refreshDimensions();
                player.getData(DataAttachments.DINO_DATA).setBuildModeUpdated(true);
                return;
            }

            player.getData(DataAttachments.DINO_DATA).setBuildModeUpdated(false);


            if (!player.level().isClientSide) {

                int newCD = player.getData(DataAttachments.ATTACK_COOLDOWN) - 1;
                player.setData(DataAttachments.ATTACK_COOLDOWN, newCD);
                PacketDistributor.sendToAllPlayers(new SyncAttackCooldown(player.getId(), newCD));



                int newKT = (player.onGround() || player.isInWater()) ? player.getData(DataAttachments.KNOCKDOWN_TIME) - 1 : player.getData(DataAttachments.KNOCKDOWN_TIME);
                player.setData(DataAttachments.KNOCKDOWN_TIME, newKT);
                PacketDistributor.sendToAllPlayers(new SyncKnockdownTime(player.getId(), newKT));

                PacketDistributor.sendToPlayersTrackingEntity(player, new SyncHeldFoodType(player.getId(), player.getData(DataAttachments.HELD_FOOD_DATA).getHeldFood().toString()));
                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncHeldFoodType(player.getId(), player.getData(DataAttachments.HELD_FOOD_DATA).getHeldFood().toString()));

                player.setData(DataAttachments.EATING_TIME, Math.max(-1, player.getData(DataAttachments.EATING_TIME) - 1));
                PacketDistributor.sendToAllPlayers(new SyncEatingTime(player.getId(), player.getData(DataAttachments.EATING_TIME)));
            }

            handleDeath(player);

            if (!player.getData(DataAttachments.DINO_DATA).isInitialized())
                initialize(player, player.getData(DataAttachments.DINO_DATA));

            if (player.tickCount % 10 == 0) {

                handleAttribute(player);

                if (!player.level().isClientSide())  {
                    PacketDistributor.sendToAllPlayers(new SyncSelectedDinosaur(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()));

                    PacketDistributor.sendToAllPlayers(new SyncIsMale(player.getId(),
                            player.getData(DataAttachments.DINO_DATA).isMale()));




                    if (player.tickCount % 100 == 0) syncSkinData(player);
                    updateRestingFactor(player);
                    handleGrowth(player);
                    handleRegenerationAndUpdateWeight(player);
                    handleWaterTick(player);
                    foodTick(player);
                    handleStamina(player);
                    updatePairs(player);
                    eggsGestationTick(player);
                    layingEggsTick(player);

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

        DinoData data = player.getData(DataAttachments.DINO_DATA);

        data.setCarbs(Math.max(0, data.getCarbs() - 0.00002f));
        data.setLipids(Math.max(0, data.getLipids() - 0.00002f));
        data.setVitamins(Math.max(0, data.getVitamins() - 0.00002f));
        data.setProteins(Math.max(0, data.getProteins() - 0.00002f));

        float newFoodLevel = (Math.max(0f,  data.getFoodLevel()
                - Util.getDino(player).getStarvationPerSec() * 0.5f));

        data.setFoodLevel(newFoodLevel);


        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncDiets(
                player.getId(),
                player.getData(DataAttachments.DINO_DATA).getCarbs(),
                player.getData(DataAttachments.DINO_DATA).getLipids(),
                player.getData(DataAttachments.DINO_DATA).getVitamins(),
                player.getData(DataAttachments.DINO_DATA).getProteins()

        ));

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

        PacketDistributor.sendToAllPlayers(new SyncSkinData(player.getId(), data.getColors()));
    }


    private static void handleDeath(Player player) {
        if ((player.getData(DataAttachments.DINO_DATA).getBloodLevel() <= 0f ||
                player.getData(DataAttachments.DINO_DATA).getHealth() <= 0f) &&
                !player.level().isClientSide()) {

            DinosaurEntity corpse = Util.getBody(player);
            corpse.setCorpse(true);
            corpse.setPos(player.getX(), player.getY(), player.getZ());

            SkinData skinData = player.getData(DataAttachments.SKIN_DATA);

            corpse.setColors(skinData.getColors());
            corpse.setBodyGrowth(player.getData(DataAttachments.DINO_DATA).getGrowth());
            corpse.setBodyRot(player.getData(DataAttachments.PLAYER_ROTATION));
            corpse.setRemainingFood(player.getData(DataAttachments.DINO_DATA).getWeight());
            corpse.setMale(player.getData(DataAttachments.DINO_DATA).isMale());

            player.level().addFreshEntity(corpse);


            player.getData(DataAttachments.DINO_DATA).fullReset();
            player.getData(DataAttachments.PAIRING_DATA).reset(true);


            player.getData(DataAttachments.ADAPTATION_DATA).fullReset();
            PacketDistributor.sendToAllPlayers(new SyncAdaptationsReset(player.getId()));

            PacketDistributor.sendToAllPlayers(new SyncFullReset(player.getId()));
        }
    }

    private static void handleGrowth(Player player) {
        float newGrowth = player.getData(DataAttachments.DINO_DATA).getGrowth()
                + (Util.getDino(player).getGrowthPerMin() / 120f) * DietStat.calculate(player, DietStat.GROWTH_SPEED);
        newGrowth = Math.min(1f, newGrowth);

        float scale = Util.getDino(player).getGrowthCurve().calculate(newGrowth, GrowthCurveStat.SCALE);
        float baseHeight = Util.getDinoBaseHeight(player);
        float baseWidth = Util.getDinoBaseWidth(player);

        if (player.getData(DataAttachments.DINO_DATA).isFlying()) baseHeight = baseHeight / 3;

        AABB hitbox = makeAABB(baseWidth * scale, baseHeight * scale, player.getX(), player.getY(), player.getZ());

        if (!player.level().noCollision(player, hitbox)) {
            newGrowth = player.getData(DataAttachments.DINO_DATA).getGrowth();
        }

        if (player.getData(DataAttachments.DINO_DATA).isInitialized()) {
            player.getData(DataAttachments.DINO_DATA).setGrowth(newGrowth);
            PacketDistributor.sendToAllPlayers(new SyncGrowth(newGrowth, player.getId()));
            player.refreshDimensions();
        }

    }

    static private AABB makeAABB(float width, float height, double x, double y, double z) {
        float f = width / 2.0F;
        float f1 = height;
        return new AABB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
    }

    private static void handleRegenerationAndUpdateWeight(Player player) {

        float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();
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
            bleedRegen = Util.getDino(player).getBleedResistance() * DietStat.calculate(player, DietStat.BLEED_RESISTANCE);
            bleedRegen = bleedRegen * player.getData(DataAttachments.REST_FACTOR) * 0.3f;
        }


        float newWeight = Util.getDino(player).getGrowthCurve().calculate(growth, GrowthCurveStat.WEIGHT);


        float healthWeightRatio = player.getData(DataAttachments.DINO_DATA).getHealth() /
                player.getData(DataAttachments.DINO_DATA).getWeight();

        float bloodWeightRatio =  player.getData(DataAttachments.DINO_DATA).getBloodLevel() /
                player.getData(DataAttachments.DINO_DATA).getWeight();

        float newHealth = (newWeight * healthWeightRatio);
        float newBloodLevel = (newWeight * bloodWeightRatio);
        float healthRegen = player.getData(DataAttachments.RESTING_STATE) == 2 ? Util.getDino(player).getHealthRegen() : 0;

        newHealth = newHealth + (healthRegen * newWeight * player.getData(DataAttachments.REST_FACTOR) * DietStat.calculate(player, DietStat.HEALTH_REGEN));

        if (!(player.getData(DataAttachments.DINO_DATA).getBleed() > 0)) {
            newBloodLevel = newBloodLevel + (Util.getDino(player).getHealthRegen() * DietStat.calculate(player, DietStat.BLOOD_REGEN) * newWeight);
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
                Adaptation data = player.getData(DataAttachments.ADAPTATION_DATA).getAdaptation(AdaptationType.SALTWATER_TOLERANCE);
                float progress = data.getProgress() + 0.0025f;


                newWaterLevel = Math.min(1f, newWaterLevel + 0.0075f);
                data.setProgress(progress);
                PacketDistributor.sendToAllPlayers(new SyncAdaptation(AdaptationType.SALTWATER_TOLERANCE, progress, data.getLevel(), player.getId(), data.isUpgraded()));

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

                player.setData(DataAttachments.VANILLA_IFRAME_COMPAT_UNTILL, player.tickCount + 200);

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

    private static Map<Integer, Double> oldYs = new HashMap<>();

    private static void handleStamina(Player player) {

        float stamina = player.getData(DataAttachments.DINO_DATA).getStamina();
        float stamPool = Util.getStaminaPool(player);
        float ratio = stamina/Math.max(1f, stamPool);
        float factor = Math.clamp(Mth.lerp(ratio, -0.5f, 1.5f), 0f, 1f);

        if (player.getData(DataAttachments.RESTING_STATE) == 2) factor = 1;
        float additionalStam = 0f;

        if (Util.getDino(player) == Dinosaurs.PTERANODON && player.getData(DataAttachments.DINO_DATA).isFlying()) {

            if (player.getData(DataAttachments.DINO_DATA).isSprinting() || player.getData(DataAttachments.DINO_DATA).isAirbraking())
                additionalStam -= 5f;


            if (oldYs.containsKey(player.getId())) if (player.getY() - oldYs.get(player.getId()) > 0.001) additionalStam -= (float) Math.min(5f, player.getY() - oldYs.get(player.getId()));
            oldYs.put(player.getId(), player.getY());

            if (additionalStam >= 0f && stamina > 1f) additionalStam = 0.8f * Util.getStamRegen(player);


        } else if (player.getData(DataAttachments.DINO_DATA).isSprinting()) {
            additionalStam = -5f;
        } else {
            additionalStam = factor * Util.getStamRegen(player)
                    * (player.getData(DataAttachments.REST_FACTOR) * 0.5f + 0.5f);
        }

        float newStam = Math.min(stamPool,
                player.getData(DataAttachments.DINO_DATA).getStamina() + additionalStam);

        player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
        PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));

        if (ratio < 0.25f) {
            Adaptation enhancedStamina = player.getData(DataAttachments.ADAPTATION_DATA).getAdaptation(AdaptationType.ENHANCED_STAMINA);

            float newProgress = enhancedStamina.getProgress() + Math.max(0f, additionalStam / (2 * Util.getStaminaPool(player)));
            enhancedStamina.setProgress(newProgress);

            PacketDistributor.sendToAllPlayers(new SyncAdaptation(AdaptationType.ENHANCED_STAMINA, newProgress,
                    enhancedStamina.getLevel(), player.getId(),
                    enhancedStamina.isUpgraded()));
        }
    }


    private static void updatePairs(Player player) {
        MinecraftServer server = player.getServer();
        LifeData data = LifeData.get(server);
        data.updateFor(player);

        UUID mateUUID = player.getData(DataAttachments.PAIRING_DATA).getMateUUID();
        if (mateUUID.equals(PairData.UNSET)) return;


        UUID mateLifeUUID = player.getData(DataAttachments.PAIRING_DATA).getMateLifeUUID();
        if (mateLifeUUID.equals(PairData.UNSET)) return;


        UUID actualMateLifeUUID = data.getLifeOf(mateUUID);
        if (actualMateLifeUUID == null) return;


        if (!mateLifeUUID.equals(actualMateLifeUUID)) {
            player.getData(DataAttachments.PAIRING_DATA).reset(false);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncPairingReset(player.getId()));
            player.sendSystemMessage(Component.literal("Your mate died so you have been unpaired."));
        }
    }

    private static void eggsGestationTick(Player player) {

        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

        if (player.getData(DataAttachments.DINO_DATA).isMale()) return;
        if (player.tickCount % 10 != 0) return;


        if (!player.getData(DataAttachments.PAIRING_DATA).isPaired()) {
            if (pairData.getGestationCountdown() >= 5400) return;
        }

        if (pairData.getStoredEggs() > 0) {
            pairData.setGestationCountdown(5400);
            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncGestationCountdown(pairData.getGestationCountdown(), player.getId()));

            return;
        }

        pairData.setGestationCountdown(pairData.getGestationCountdown() - 1);
        PacketDistributor.sendToPlayer((ServerPlayer) player,
                new SyncGestationCountdown(pairData.getGestationCountdown(), player.getId()));

        if (pairData.getGestationCountdown() <= 0) {
            pairData.setStoredEggs(Util.getDino(player).getMaxEggs());
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncStoredEggs(pairData.getStoredEggs(), player.getId()));
        }
    }

    private static void layingEggsTick(Player player) {
        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        NestData nestData = NestData.get(player.level().getServer());

        if (dinoData.isMale()) return;
        if (player.tickCount % 70 != 0) return;

        if (!pairData.isPaired() && dinoData.isLayingEggs()) {
            dinoData.setLayingEggs(false);
            PacketDistributor.sendToAllPlayers(new SyncLayingEggs(false, player.getId()));
            return;
        }

        if (!pairData.isPaired()) return;

        Nest nest = nestData.getNestByPlayer(player);

        if (nest == null) return;

        if (pairData.getStoredEggs() <= 0 || nest.getEggsCount() >= nest.getMaxEggsCount()) {
            dinoData.setLayingEggs(false);
            PacketDistributor.sendToAllPlayers(new SyncLayingEggs(false, player.getId()));
            return;
        }

        if (dinoData.isLayingEggs()) {
            nestData.addEgg(nest, (ServerPlayer) player);
        }
    }
}
