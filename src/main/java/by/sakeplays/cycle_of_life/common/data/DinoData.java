package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class DinoData implements INBTSerializable<CompoundTag> {


    private int selectedDinosaur;
    private int pairingWith;
    private float stamina;
    private float acceleration;
    private float foodLevel;
    private float waterLevel;
    private float turnDegree;
    private float health;
    private float growth;
    private float bloodLevel;
    private float bleed;
    private float weight;
    private boolean isSprinting;
    private boolean isMoving;
    private boolean isMale;
    private boolean isSliding;
    private boolean isInitialized;
    private boolean isPaired;
    private boolean isDrinking;

    public DinoData(int selectedDino, float stamina, float foodLevel, float waterLevel,
                    float turnDegree, float health, boolean isSprinting, boolean isMoving, float acceleration,
                    float growth, float weight, boolean isInitialized, float bloodLevel, float bleed,
                    boolean isSliding) {
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
        this.isInitialized = isInitialized;
        this.bloodLevel = bloodLevel;
        this.bleed = bleed;
        this.isSliding = isSliding;

        isMale = Math.random() < 0.5;

        pairingWith = 0;
        isPaired = false;
        isDrinking = false;



    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("SelectedDinosaur", selectedDinosaur);
        nbt.putInt("PairingWith", pairingWith);
        nbt.putFloat("Stamina", stamina);
        nbt.putFloat("FoodLevel", foodLevel);
        nbt.putFloat("WaterLevel", waterLevel);
        nbt.putFloat("TurnDegree", turnDegree);
        nbt.putFloat("Health", health);
        nbt.putBoolean("IsSprinting", isSprinting);
        nbt.putBoolean("IsInitialized", isInitialized);
        nbt.putFloat("Acceleration", acceleration);
        nbt.putFloat("Growth", growth);
        nbt.putFloat("Weight", weight);
        nbt.putFloat("BloodLevel", bloodLevel);
        nbt.putFloat("Bleed", bleed);
        nbt.putBoolean("IsMale", isMale);
        nbt.putBoolean("IsPaired", isPaired);
        nbt.putBoolean("IsDrinking", isDrinking);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.selectedDinosaur = nbt.getInt("SelectedDinosaur");
        this.pairingWith = nbt.getInt("PairingWith");
        this.stamina = nbt.getFloat("Stamina");
        this.foodLevel = nbt.getFloat("FoodLevel");
        this.waterLevel = nbt.getFloat("WaterLevel");
        this.turnDegree = nbt.getFloat("TurnDegree");
        this.health = nbt.getFloat("Health");
        this.isSprinting = nbt.getBoolean("IsSprinting");
        this.isInitialized = nbt.getBoolean("IsInitialized");
        this.acceleration = nbt.getFloat("Acceleration");
        this.growth = nbt.getFloat("Growth");
        this.weight = nbt.getFloat("Weight");
        this.bleed = nbt.getFloat("Bleed");
        this.bloodLevel = nbt.getFloat("BloodLevel");
        this.isMale = nbt.getBoolean("IsMale");
        this.isPaired = nbt.getBoolean("IsPaired");
        this.isDrinking = nbt.getBoolean("IsDrinking");

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

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public float getBloodLevel() {
        return bloodLevel;
    }

    public float getBleed() {
        return bleed;
    }

    public void setBloodLevel(float bloodLevel) {
        this.bloodLevel = bloodLevel;
    }

    public void setBleed(float bleed) {
        this.bleed = bleed;
    }


    public void fullReset() {
        setSelectedDinosaur(0);
        setInitialized(false);

        setStamina(1);
        setFoodLevel(1);
        setWaterLevel(1);
        setWeight(1);
        setGrowth(0.25f);
        setBleed(0);
        setHealth(1);
        setAcceleration(0f);
        setBloodLevel(1);
        setSprinting(false);
        setMoving(false);
        setPairingWith(0);
        setPaired(false);
        isMale = Math.random() < 0.5;


    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }


    public boolean isSliding() {
        return isSliding;
    }

    public void setSliding(boolean sliding) {
        isSliding = sliding;
    }

    public int getPairingWith() {
        return pairingWith;
    }

    public void setPairingWith(int pairingWith) {
        this.pairingWith = pairingWith;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }

    public boolean isDrinking() {
        return isDrinking;
    }

    public void setDrinking(boolean drinking) {
        isDrinking = drinking;
    }
}


