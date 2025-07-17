package by.sakeplays.cycle_of_life.common.data.adaptations;

import com.mojang.datafixers.util.Function3;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiFunction;

public class Adaptation {


    private int level;
    private float progress;
    private boolean isUpgraded;

    public Adaptation(int level, float progress, boolean isUpgraded) {
        this.level = level;
        this.progress = progress;
        this.isUpgraded = isUpgraded;

    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putFloat("Progress", progress);
        tag.putInt("Level", level);
        tag.putBoolean("IsUpgraded", isUpgraded);

        return tag;
    }


    public static <T extends Adaptation> T fromNBT(CompoundTag tag, Function3<Integer, Float, Boolean, T> constructor) {
        int level = tag.getInt("Level");
        float progress = tag.getFloat("Progress");
        boolean upgraded = tag.getBoolean("IsUpgraded");

        return constructor.apply(level, progress, upgraded);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {

        if (isUpgraded) return;

        this.progress = Math.min(1f, progress);

        if (this.progress >= 1f) {
            this.progress = 0;
            setLevel(getLevel() + 1);
            setUpgraded(true);
        }
    }

    public float getValue(int level) {
        switch (level) {
            case 0 -> {
                return 0f;
            }
            case 1 -> {
                return 1f;
            }
            case 2 -> {
                return 2f;
            }
            case 3 -> {
                return 3f;
            }
            case 4 -> {
                return 4f;
            }
            case 5 -> {
                return 5f;
            }
        }
        return 0;
    }

    public boolean isUpgraded() {
        return isUpgraded;
    }

    public void setUpgraded(boolean upgraded) {
        isUpgraded = upgraded;
    }
}
