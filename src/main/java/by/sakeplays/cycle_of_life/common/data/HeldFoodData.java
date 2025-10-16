package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class HeldFoodData implements INBTSerializable<CompoundTag> {

    private DinosaurFood heldFoodItem;
    private float foodWeight;

    public HeldFoodData() {
        heldFoodItem = DinosaurFood.FOOD_NONE;
        foodWeight = 0f;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("HeldFoodItem", heldFoodItem.toString());
        nbt.putFloat("HeldFoodWeight", foodWeight);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.heldFoodItem = DinosaurFood.fromString(nbt.getString("HeldFoodItem"));
        this.foodWeight = nbt.getFloat("HeldFoodWeight");
    }

    public DinosaurFood getHeldFood() {
        return heldFoodItem;
    }

    public void setHeldFoodItem(DinosaurFood heldFoodItem) {
        this.heldFoodItem = heldFoodItem;
    }

    public float getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(float foodWeight) {
        this.foodWeight = foodWeight;
    }
}


