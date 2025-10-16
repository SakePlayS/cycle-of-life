package by.sakeplays.cycle_of_life.common.data.adaptations;

import java.util.List;

public enum AdaptationType {
    SALTWATER_TOLERANCE(0.33f, 0.01f, 0.005f, 0.0025f, 0.0015f, 0f),
    ENHANCED_STAMINA(0, 0.1f, 0.125f, 0.15f, 0.175f, 0.2f),
    BLEED_RESISTANCE(0, 0.1f, 0.125f, 0.15f, 0.175f, 0.2f),
    COLD_RESISTANCE(0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f),
    HEAT_RESISTANCE(0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f);

    private final float val0;
    private final float val1;
    private final float val2;
    private final float val3;
    private final float val4;
    private final float val5;

    AdaptationType(float lvl0, float lvl1, float lvl2, float lvl3, float lvl4, float lvl5) {
        this.val0 = lvl0;
        this.val1 = lvl1;
        this.val2 = lvl2;
        this.val3 = lvl3;
        this.val4 = lvl4;
        this.val5 = lvl5;
    }

    public String adaptationName() {
        return this.toString().toLowerCase();
    }

    public static AdaptationType fromName(String name) {
        for (AdaptationType type : AdaptationType.values()) {
            if (type.adaptationName().equals(name)) return type;
        }

        return null;
    }

    public float getValue(int level) {

        if (level >= 6) return val5;

        switch (level) {
            case 1 -> {return val1;}
            case 2 -> {return val2;}
            case 3 -> {return val3;}
            case 4 -> {return val4;}
            case 5 -> {return val5;}
            default -> {return val0;}
        }
    }

    public static void initialize(List<Adaptation> adaptationList) {
        for (AdaptationType type : AdaptationType.values()) {
            adaptationList.add(new Adaptation(0, 0, false, type));
        }
    }
}
