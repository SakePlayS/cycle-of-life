package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class SkinData implements INBTSerializable<CompoundTag> {


    private SelectedColors colors;

    public SkinData() {
        colors = new SelectedColors();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        colors.toNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        colors = SelectedColors.fromNBT(nbt);
    }

    public void setColor(ColorableBodyParts part, int primary, int secondary) {
        colors.setColor(part, primary, secondary);
    }

    public Pair<Integer, Integer> getColor(ColorableBodyParts part) {
        return colors.getColor(part);
    }

    public SelectedColors getColors() {
        return colors;
    }

}


