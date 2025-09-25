package by.sakeplays.cycle_of_life.common.data.adaptations;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiFunction;

public class Adaptation {

    private int level;
    private float progress;
    private boolean isUpgraded;
    private final String NAME;
    private final AdaptationType TYPE;

    public Adaptation(int level, float progress, boolean isUpgraded, AdaptationType type) {
        this.level = level;
        this.progress = progress;
        this.isUpgraded = isUpgraded;
        this.NAME = type.adaptationName();
        this.TYPE = type;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("Name", TYPE.adaptationName());
        tag.putFloat("Progress", progress);
        tag.putInt("Level", level);
        tag.putBoolean("IsUpgraded", isUpgraded);

        return tag;
    }


    public static Adaptation fromNBT(CompoundTag tag) {
        int level = tag.getInt("Level");
        float progress = tag.getFloat("Progress");
        boolean upgraded = tag.getBoolean("IsUpgraded");
        String name = tag.getString("Name");
        AdaptationType adaptationType = AdaptationType.fromName(name);

        if (adaptationType == null) throw new IllegalArgumentException("Unknown AdaptationType: " + name);

        return new Adaptation(level, progress, upgraded, adaptationType);
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

    public boolean isUpgraded() {
        return isUpgraded;
    }

    public void setUpgraded(boolean upgraded) {
        isUpgraded = upgraded;
    }

    public AdaptationType getType() {
        return TYPE;
    }

    public String getName() {
        return NAME;
    }

}
