package by.sakeplays.cycle_of_life.entity.util;

public enum HitboxType {

    HEAD,
    BODY1,
    BODY2,
    TAIL1,
    TAIL2;


    public static HitboxType fromString(String s) {
        if (s.equalsIgnoreCase("HEAD")) return HEAD;
        if (s.equalsIgnoreCase("BODY1")) return BODY1;
        if (s.equalsIgnoreCase("BODY2")) return BODY2;
        if (s.equalsIgnoreCase("TAIL1")) return TAIL1;

        return TAIL2;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
