package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public class StatusData implements INBTSerializable<CompoundTag> {


    private float saltwaterSicknessStage;
    private boolean isGestatingEggs;

    public StatusData() {
        saltwaterSicknessStage = 0;
        isGestatingEggs = false;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("SaltwaterSicknessStage", saltwaterSicknessStage);
        nbt.putBoolean("IsGestatingEggs", isGestatingEggs);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.saltwaterSicknessStage = nbt.getInt("SaltwaterSicknessStage");
        this.isGestatingEggs = nbt.getBoolean("IsGestatingEggs");

    }

    public void reset() {
        saltwaterSicknessStage = 0;
        isGestatingEggs = false;
    }

    public float getSaltwaterSicknessStage() {
        return saltwaterSicknessStage;
    }

    public void setSaltwaterSicknessStage(float stage) {
        this.saltwaterSicknessStage = Math.max(0, Math.min(4f, stage));
    }

    public boolean isGestatingEggs() {
        return isGestatingEggs;
    }

    public void setGestatingEggs(boolean gestatingEggs) {
        isGestatingEggs = gestatingEggs;
    }
}


