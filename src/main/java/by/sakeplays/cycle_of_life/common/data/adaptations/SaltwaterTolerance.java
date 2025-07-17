package by.sakeplays.cycle_of_life.common.data.adaptations;

import net.minecraft.nbt.CompoundTag;

public class SaltwaterTolerance extends Adaptation {
    public SaltwaterTolerance(int level, float progress, boolean isUpgraded) {
        super(level, progress, isUpgraded);
    }

    @Override
    public float getValue(int level) {
        switch (level) {
            case 0 -> {
                return 0.05f;
            }
            case 1 -> {
                return 0.01f;
            }
            case 2 -> {
                return 0.005f;
            }
            case 3 -> {
                return 0.0025f;
            }
            case 4 -> {
                return 0.001f;
            }
            case 5 -> {
                return 0f;
            }
        }
        return 0.05f;
    }


}
