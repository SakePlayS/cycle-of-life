package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;

public class DinoData implements INBTSerializable<CompoundTag> {


    private int selectedDinosaur;
    private float stamina;
    private float acceleration;
    private float foodLevel;
    private float waterLevel;
    private float turnDegree;
    private float health;
    private float growth;
    private float weight;

    private boolean isSprinting;
    private boolean isMoving;

    public DinoData(int selectedDino, float stamina, float foodLevel, float waterLevel,
                    float turnDegree, float health, boolean isSprinting, boolean isMoving, float acceleration,
                    float growth, float weight) {
        this.selectedDinosaur = selectedDino;
        this.stamina = stamina;
        this.foodLevel = foodLevel;
        this.waterLevel = waterLevel;
        this.turnDegree = turnDegree;
        this.health = health;
        this.isSprinting = isSprinting;
        this.isMoving = isMoving;
        this.acceleration = acceleration;
        this.growth = growth;
        this.weight = weight;


    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("SelectedDinosaur", selectedDinosaur);
        nbt.putFloat("Stamina", stamina);
        nbt.putFloat("FoodLevel", foodLevel);
        nbt.putFloat("WaterLevel", waterLevel);
        nbt.putFloat("TurnDegree", turnDegree);
        nbt.putFloat("Health", health);
        nbt.putBoolean("IsSprinting", isSprinting);
        nbt.putFloat("Acceleration", acceleration);
        nbt.putFloat("Growth", growth);
        nbt.putFloat("Weight", weight);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.selectedDinosaur = nbt.getInt("SelectedDinosaur");
        this.stamina = nbt.getFloat("Stamina");
        this.foodLevel = nbt.getFloat("FoodLevel");
        this.waterLevel = nbt.getFloat("WaterLevel");
        this.turnDegree = nbt.getFloat("TurnDegree");
        this.health = nbt.getFloat("Health");
        this.isSprinting = nbt.getBoolean("IsSprinting");
        this.acceleration = nbt.getFloat("Acceleration");
        this.growth = nbt.getFloat("Growth");
        this.weight = nbt.getFloat("Weight");


    }

    public void setSelectedDinosaur(int selectedDinosaur) {
        this.selectedDinosaur = selectedDinosaur;
    }

    public int getSelectedDinosaur() {
        return selectedDinosaur;
    }

    public void setStamina(float stamina) {
        this.stamina = stamina;
    }

    public void setFoodLevel(float foodLevel) {
        this.foodLevel = foodLevel;
    }

    public void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setTurnDegree(float turnDegree) {
        this.turnDegree = turnDegree;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    public float getStamina() {
        return stamina;
    }

    public float getFoodLevel() {
        return foodLevel;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public float getTurnDegree() {
        return turnDegree;
    }

    public float getHealth() {
        return health;
    }

    public boolean isSprinting() {
        return isSprinting;
    }


    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getGrowth() {
        return growth;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setGrowth(float growth) {
        this.growth = growth;
    }

}


