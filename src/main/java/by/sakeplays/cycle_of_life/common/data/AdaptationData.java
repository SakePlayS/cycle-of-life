package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.common.data.adaptations.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class AdaptationData implements INBTSerializable<CompoundTag> {

    public List<Adaptation> adaptations = new ArrayList<>();

    public AdaptationData() {
        AdaptationType.initialize(adaptations);
    }


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        for (Adaptation adaptation : adaptations) {
            nbt.put(adaptation.getName(), adaptation.toNBT());
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        adaptations.clear();
        for (String key : nbt.getAllKeys()) {
            adaptations.add(Adaptation.fromNBT(nbt.getCompound(key)));
        }

    }

    public void fullReset() {
        adaptations.clear();

        AdaptationType.initialize(adaptations);
    }

    public Adaptation getAdaptation(AdaptationType type) {
        for (Adaptation adaptation : adaptations) {
            if (adaptation.getType().equals(type)) return adaptation;
        }

        return null;
    }

    public AdaptationsHolder toHolder() {
        AdaptationsHolder holder = new AdaptationsHolder();

        for (Adaptation adaptation : adaptations) {
            holder.addEntry(adaptation.getName(), adaptation.getLevel());
        }

        return holder;
    }

    public List<Adaptation> getAdaptationList() {
        return adaptations;
    }
}
