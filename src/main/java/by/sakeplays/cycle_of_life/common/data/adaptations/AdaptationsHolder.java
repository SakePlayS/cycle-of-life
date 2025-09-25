package by.sakeplays.cycle_of_life.common.data.adaptations;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class AdaptationsHolder {

    private Map<String, Integer> adaptations = new HashMap<>();

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        adaptations.forEach(tag::putInt);

        return tag;
    }

    public static AdaptationsHolder fromNBT(CompoundTag tag) {
        AdaptationsHolder holder = new AdaptationsHolder();

        for (String key : tag.getAllKeys()) {
            holder.addEntry(key, tag.getInt(key));
        }

        return holder;
    }

    public void addEntry(String key, int value) {
        adaptations.put(key, value);
    }

    public Map<String, Integer> getMap() {
        return new HashMap<>(adaptations);
    }
}
