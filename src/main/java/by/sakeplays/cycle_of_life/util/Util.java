package by.sakeplays.cycle_of_life.util;

import by.sakeplays.cycle_of_life.ModSounds;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationType;
import by.sakeplays.cycle_of_life.entity.*;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncBleed;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncHealth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_server.RequestPlayHurtSound;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

public class Util {

    public static float getTurnSpeed(Player player) {
        return getDino(player).getTurnSpeed();

    }

    public static float getStamRegen(Player player) {
        return getDino(player).getStaminaRegen();

    }

    public static float getWalkSpeed(Player player) {
        return getDino(player).getWalkSpeed();

    }

    public static float getSprintSpeed(Player player) {
        return getDino(player).getSprintSpeed();
    }

    public static float getAcceleration(Player player) {
        return getDino(player).getAcceleration();

    }

    public static float getSwimSpeed(Player player) {
        return getDino(player).getSwimSpeed();
    }

    public static float getTurnPenalty(Player player) {
        if (!player.isInWater() && !player.onGround()) {
            return 0.1F;
        }

        return 1f;
    }

    public static Dinosaurs getDino(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        return Dinosaurs.getById(ID);
    }


    public static void recordYHistory(Player player, float y) {
        player.getData(DataAttachments.Y_HISTORY).add(y);

        if (player.getData(DataAttachments.Y_HISTORY).size() > 6) {
            player.getData(DataAttachments.Y_HISTORY).removeFirst();
        }
    }

    public static void recordTurnHistory(Player player, float y) {
        player.getData(DataAttachments.TURN_HISTORY).add(y);

        if (player.getData(DataAttachments.TURN_HISTORY).size() > 7) {
            player.getData(DataAttachments.TURN_HISTORY).removeFirst();
        }
    }


    public static float calculateTailXRot(ArrayList<Float> arrayList) {
        float sum = 0;
        int iterations = 0;

        for (int i = 0; i < arrayList.size() - 1; i++) {
            sum = sum + (arrayList.get(i + 1) - arrayList.get(i)) * Mth.DEG_TO_RAD;
            iterations++;
        }

        return sum/iterations;
    }

    public static float calculateTailYRot(ArrayList<Float> arrayList, float currentTurnDegree, int lowerBound, int upperBound) {
        float sum = 0;
        int iterations = 0;

        for (int i = lowerBound; i < upperBound; i++) {
            sum = sum + (currentTurnDegree - arrayList.get(i)) * Mth.DEG_TO_RAD;
            iterations++;
        }

        return sum/iterations;
    }

    public static void dealDamage(Player target, float dmg, float bleed, boolean playHurtSound) {
        if (target.level().isClientSide) {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newBleed = data.getBleed() + bleed;
            float newHealth = data.getHealth() - dmg;

            data.setBleed(newBleed);
            PacketDistributor.sendToServer(new SyncBleed(target.getId(),newBleed));

            data.setHealth(newHealth);
            PacketDistributor.sendToServer(new SyncHealth(target.getId(), newHealth));

            if (playHurtSound) PacketDistributor.sendToServer(new RequestPlayHurtSound(target.getId()));

        } else {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newBleed = data.getBleed() + bleed;
            float newHealth = data.getHealth() - dmg;

            data.setBleed(newBleed);
            PacketDistributor.sendToAllPlayers(new SyncBleed(target.getId(),newBleed));

            data.setHealth(newHealth);
            PacketDistributor.sendToAllPlayers(new SyncHealth(target.getId(), newHealth));

            if (playHurtSound)  {
                int dinoId = Util.getDino(target).getID();

                switch (dinoId) {
                    case 2 -> target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            ModSounds.DEINONYCHUS_HURT.get(), SoundSource.PLAYERS, 1f ,1f +
                                    (float) ((Math.random() - 0.5) / 4));
                    default -> target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1f ,1f +
                                    (float) ((Math.random() - 0.5) / 4));
                }
            }
        }
    }

    public static void addStamina(Player target, float stamina) {
        if (target.level().isClientSide) {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newStam = data.getStamina() + stamina;

            if (newStam >  Util.getStaminaUpgraded(target)) newStam = Util.getStaminaUpgraded(target);

            target.getData(DataAttachments.DINO_DATA).setStamina(newStam);
            PacketDistributor.sendToServer(new SyncStamina(target.getId(), newStam));

        }
    }

    public static int rgbaToInt(float r, float g, float b, float a) {
        int alpha = (int)(a * 255.0f) << 24;
        int red   = (int)(r * 255.0f) << 16;
        int green = (int)(g * 255.0f) << 8;
        int blue  = (int)(b * 255.0f);
        return alpha | red | green | blue;
    }

    public static int rgbaToInt(int r, int g, int b, int a) {
        int alpha = a << 24;
        int red   = r << 16;
        int green = g << 8;
        return alpha | red | green | b;
    }

    public static float getStaminaUpgraded(Player player) {
        Adaptation data = player.getData(DataAttachments.ADAPTATION_DATA).getAdaptation(AdaptationType.ENHANCED_STAMINA);


        return getDino(player).getStaminaPool() * (1 + data.getType().getValue(data.getLevel()));
    }

    public static DinosaurEntity getBody(Player player) {
        int dinoId = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (dinoId == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return new Pachycephalosaurus(ModEntities.PACHYCEPHALOSAURUS.get(), player.level());
        if (dinoId == Dinosaurs.DEINONYCHUS.getID()) return new Deinonychus(ModEntities.DEINONYCHUS.get(), player.level());

        // fallback
        return new Deinonychus(ModEntities.DEINONYCHUS.get(), player.level());
    }

    public static float calculateScale(DinosaurEntity entity, float lowerBound, float upperBound) {

        if (entity.isBody()) return Mth.lerp(entity.getBodyGrowth(), lowerBound, upperBound);
        if (entity.isForScreenRendering) return 1;
        return Mth.lerp(entity.getPlayer().getData(DataAttachments.DINO_DATA).getGrowth(), lowerBound, upperBound);

    }


    public static float calculateMaxSpeed(Player player) { // used for display
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float growth = data.getGrowth();
        float maxSpeed = Util.getDino(player).getSprintSpeed();
        float adjustedSpeed = (float) Math.pow(growth, 1f/2f);
        maxSpeed = maxSpeed * (Mth.lerp(adjustedSpeed, 0.1f, 1f) * 20);

        return maxSpeed;
    }


    public static float calculateSpeed(Player player) {  // used for actual movement
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float growth = data.getGrowth();
        float speed = data.isSprinting() ? Util.getDino(player).getSprintSpeed() : Util.getDino(player).getWalkSpeed();


        float adjustedSpeed = (float) Math.pow(growth, 0.5f);
        speed = speed * (Mth.lerp(adjustedSpeed, 0.1f, 1f));

        if (player.isInWater()) {
            speed *= Util.getSwimSpeed(player);
        }

        return speed;
    }

    public static boolean isAttackValid(Player source, Player target) {

        /// the hitbox may vary in size. if the dino player is big they have higher attack distance.
        if (source.distanceTo(target) > 7 * (source.getBoundingBox().getXsize() + 0.5)) return false;

        return true;
    }

    public static float calculateDamageFactor(HitboxType type, Player target) {

        if (Util.getDino(target).getID() == Dinosaurs.PACHYCEPHALOSAURUS.getID() && type == HitboxType.HEAD) return 0.5f;

        if (type == HitboxType.HEAD) return 1.66f;
        if (type == HitboxType.BODY1) return 1f;
        if (type == HitboxType.BODY2) return 0.75f;
        if (type == HitboxType.TAIL1) return 0.5f;
        if (type == HitboxType.TAIL2) return 0.25f;

        return 0f;
    }


    public static boolean attemptToHitPlayer(Player target, float damage, float bleed, boolean makeNoise, HitboxType type) {

        float damageModifier = calculateDamageFactor(type, target);

        dealDamage(target, damage * damageModifier, bleed * damageModifier, makeNoise);
        return true;

    }

}
