package by.sakeplays.cycle_of_life.client.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class Util {


    public static float smoothify(float f) {
        if (f > 0) {
            return f = Math.min(0.03f, f);
        }

        return f = Math.max(-0.03f, f);

    }

    public static Vec3 getPos(Player player, float tailLevel) {
        return new Vec3(player.getX(), player.getY() + tailLevel, player.getZ());
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


}
