package by.sakeplays.cycle_of_life.common.data.adaptations;

public class ColdResistance extends Adaptation {
    public ColdResistance(int level, float progress, boolean isUpgraded) {
        super(level, progress, isUpgraded);
    }

    @Override
    public float getValue(int level) {
        switch (level) {
            case 0 -> {
                return 0.0f;
            }
            case 1 -> {
                return 0.1f;
            }
            case 2 -> {
                return 0.125f;
            }
            case 3 -> {
                return 0.15f;
            }
            case 4 -> {
                return 0.175f;
            }
            case 5 -> {
                return 0.2f;
            }
        }
        return 0.0f;
    }


}
