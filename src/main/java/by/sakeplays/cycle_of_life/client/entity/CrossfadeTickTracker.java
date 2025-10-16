package by.sakeplays.cycle_of_life.client.entity;

import java.util.HashMap;
import java.util.Map;

public class CrossfadeTickTracker {
    private static Map<Integer, Double> crossfadeMap = new HashMap<>();

    public static void addOrReplace(int playerId, double tick) {
        if (crossfadeMap.containsKey(playerId)) {
            crossfadeMap.replace(playerId, tick);
            return;
        }
        CrossfadeTickTracker.crossfadeMap.put(playerId, tick);
    }

    public static double getCrossfadeTick(int playerId) {
        return crossfadeMap.containsKey(playerId) ? crossfadeMap.get(playerId) : 0d;
    }
}
