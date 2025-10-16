package by.sakeplays.cycle_of_life.entity.util;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum HitboxType {

    NONE(0, 0f),
    HEAD(5, 1.5f),
    BODY1(4, 1f),
    BODY2(3, 0.8f),
    TAIL1(2, 0.5f),
    TAIL2(1, 0.25f);


    private final int priority;
    private final float damageModifier;

    HitboxType(int priority, float damageModifier) {
        this.priority = priority;
        this.damageModifier = damageModifier;
    }

    public static HitboxType fromString(String s) {

        if (s == null) return NONE;

        for (HitboxType hb : HitboxType.values()) {
            if (hb.name().equalsIgnoreCase(s)) return hb;
        }

        return NONE;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static HitboxType withHighestPriority(List<HitboxType> hitboxes) {
        return hitboxes.isEmpty() ? HitboxType.NONE : Collections.max(hitboxes, Comparator.comparingInt(HitboxType::getPriority));
    }

    public int getPriority() {
        return priority;
    }

    public float getDamageModifier() {
        return damageModifier;
    }
}
