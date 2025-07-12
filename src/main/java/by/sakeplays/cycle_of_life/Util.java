package by.sakeplays.cycle_of_life;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncBleed;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncHealth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_server.RequestPlayHurtSound;
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

        if (ID == 1) return  Dinosaurs.PACHYCEPHALOSAURUS;
        return  Dinosaurs.DEINONYCHUS;

    }


    public static void recordYHistory(Player player, float y) {
        player.getData(DataAttachments.Y_HISTORY).add(y);

        if (player.getData(DataAttachments.Y_HISTORY).size() > 6) {
            player.getData(DataAttachments.Y_HISTORY).removeFirst();
        }
    }

    public static void recordTurnHistory(Player player, float y) {
        player.getData(DataAttachments.TURN_HISTORY).add(y);

        if (player.getData(DataAttachments.TURN_HISTORY).size() > 9) {
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

    public static float calculateTailYRot(ArrayList<Float> arrayList, float currentTurnDegree) {
        float sum = 0;
        int iterations = 0;

        for (int i = 0; i < arrayList.size(); i++) {
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

        }
    }

    public static void addStamina(Player target, float stamina) {
        if (target.level().isClientSide) {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newStam = data.getStamina() + stamina;

            if (newStam >  getDino(target).getStaminaPool()) newStam = getDino(target).getStaminaPool();

            data.setStamina(newStam);
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


}
