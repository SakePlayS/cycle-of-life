package by.sakeplays.cycle_of_life.common.data.adaptations;

import net.minecraft.nbt.CompoundTag;

import java.util.function.BiFunction;

public class EnhancedStamina extends Adaptation{
    public EnhancedStamina(int level, float progress, boolean isUpgraded) {
        super(level, progress, isUpgraded);
    }

    @Override
    public float getValue(int level) {
        switch (level) {
            case 0 -> {
                return 0.0f;
            }
            case 1 -> {
                return 0.075f;
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
        return 0.05f;
    }

}
