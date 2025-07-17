package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.common.data.adaptations.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class AdaptationData implements INBTSerializable<CompoundTag> {

    public AdaptationData() {
        SALTWATER_TOLERANCE = new SaltwaterTolerance(0, 0, false);
        ENHANCED_STAMINA = new EnhancedStamina(0, 0, false);
        BLEED_RESISTANCE = new BleedResistance(0, 0, false);
        HEAT_RESISTANCE = new HeatResistance(0, 0, false);
        COLD_RESISTANCE = new ColdResistance(0, 0, false);
    }

    public SaltwaterTolerance SALTWATER_TOLERANCE;
    public EnhancedStamina ENHANCED_STAMINA;
    public BleedResistance BLEED_RESISTANCE;
    public HeatResistance HEAT_RESISTANCE;
    public ColdResistance COLD_RESISTANCE;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        nbt.put("SaltwaterTolerance", SALTWATER_TOLERANCE.toNBT());
        nbt.put("EnhancedStamina",  ENHANCED_STAMINA.toNBT());
        nbt.put("BleedResistance",  BLEED_RESISTANCE.toNBT());
        nbt.put("HeatResistance",  HEAT_RESISTANCE.toNBT());
        nbt.put("ColdResistance",  COLD_RESISTANCE.toNBT());

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

        this.SALTWATER_TOLERANCE = Adaptation.fromNBT(nbt.getCompound("SaltwaterTolerance"), SaltwaterTolerance::new);
        this.ENHANCED_STAMINA = Adaptation.fromNBT(nbt.getCompound("EnhancedStamina"), EnhancedStamina::new);
        this.BLEED_RESISTANCE = Adaptation.fromNBT(nbt.getCompound("BleedResistance"), BleedResistance::new);
        this.HEAT_RESISTANCE = Adaptation.fromNBT(nbt.getCompound("HeatResistance"), HeatResistance::new);
        this.COLD_RESISTANCE = Adaptation.fromNBT(nbt.getCompound("ColdResistance"), ColdResistance::new);

    }

    public void fullReset() {
        this.ENHANCED_STAMINA = new EnhancedStamina(0 ,0, false);
        this.SALTWATER_TOLERANCE = new SaltwaterTolerance(0, 0, false);
        this.BLEED_RESISTANCE = new BleedResistance(0, 0, false);
        this.HEAT_RESISTANCE = new HeatResistance(0, 0, false);
        this.COLD_RESISTANCE = new ColdResistance(0, 0, false);

    }
}
