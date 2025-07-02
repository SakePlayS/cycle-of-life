package by.sakeplays.cycle_of_life;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.util.DinosaursList;
import net.minecraft.world.entity.player.Player;

public class Util {

    public static float getTurnSpeed(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (ID == DinosaursList.PACHYCEPHALOSAURUS.getID()) return DinosaursList.PACHYCEPHALOSAURUS.getTurnSpeed();
        if (ID == DinosaursList.DEINONYCHUS.getID()) return DinosaursList.DEINONYCHUS.getTurnSpeed();

        return 0f;
    }

    public static float getWalkSpeed(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (ID == DinosaursList.PACHYCEPHALOSAURUS.getID()) return DinosaursList.PACHYCEPHALOSAURUS.getWalkSpeed();
        if (ID == DinosaursList.DEINONYCHUS.getID()) return DinosaursList.DEINONYCHUS.getWalkSpeed();

        return 0f;
    }

    public static float getSprintSpeed(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (ID == DinosaursList.PACHYCEPHALOSAURUS.getID()) return DinosaursList.PACHYCEPHALOSAURUS.getSprintSpeed();
        if (ID == DinosaursList.DEINONYCHUS.getID()) return DinosaursList.DEINONYCHUS.getSprintSpeed();

        return 0f;
    }

    public static float getAcceleration(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (ID == DinosaursList.PACHYCEPHALOSAURUS.getID()) return DinosaursList.PACHYCEPHALOSAURUS.getAcceleration();
        if (ID == DinosaursList.DEINONYCHUS.getID()) return DinosaursList.DEINONYCHUS.getAcceleration();

        return 0f;
    }

    public static float getSwimSpeed(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (ID == DinosaursList.PACHYCEPHALOSAURUS.getID()) return DinosaursList.PACHYCEPHALOSAURUS.getSwimSpeed();
        if (ID == DinosaursList.DEINONYCHUS.getID()) return DinosaursList.DEINONYCHUS.getSwimSpeed();

        return 0f;
    }

    public static float getTurnPenalty(Player player) {
        if (!player.isInWater() && !player.onGround()) {
            return 0.1F;
        }

        return 1f;
    }

    public static DinosaursList getDino(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (ID == 1) return  DinosaursList.PACHYCEPHALOSAURUS;
        return  DinosaursList.DEINONYCHUS;

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
}
