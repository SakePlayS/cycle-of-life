package by.sakeplays.cycle_of_life.entity.util;

public enum DinosaursList {


    PACHYCEPHALOSAURUS(0.00166f, 425f, 3f, 4f, 7.5f, 2f,
            0.1f, 1750f, 350, 0.00015f, 0.00015f,
            Diet.HERBIVORE, false, 1),

    DEINONYCHUS(0.002f,75f, 5f, 12f, 13f, 3f,
            0.25f, 1000f, 200, 0.00008f, 0.00012f,
            Diet.CARNIVORE, false, 2);

    private final float weight;
    private final float heatResistance;
    private final float coldResistance;
    private final float turnSpeed;
    public final int ID;
    private final float bleedResistance;
    private final float staminaRegen;
    private final float staminaPool;
    private final Diet diet;
    private final boolean cannibalistic;
    private final int scentRange;
    private final float starvationPerSec;
    private final float dehydrationPerSec;


    DinosaursList(float growthPerMin, float weight, float heatResistance, float coldResistance, float turnSpeed,
                  float bleedResistance, float staminaRegen, float staminaPool, int scentRange,
                  float starvationPerSec, float dehydrationPerSec, Diet diet, boolean cannibalistic, int ID) {
        this.weight = weight;
        this.heatResistance = heatResistance;
        this.bleedResistance = bleedResistance;
        this.staminaPool = staminaPool;
        this.coldResistance = coldResistance;
        this.turnSpeed = turnSpeed;
        this.staminaRegen = staminaRegen;
        this.diet = diet;
        this.cannibalistic = cannibalistic;
        this.scentRange = scentRange;
        this.starvationPerSec = starvationPerSec;
        this.dehydrationPerSec = dehydrationPerSec;
        this.ID = ID;
    }

    public float getWeight() {
        return this.weight;
    }

    public float getHeatResistance() {
        return heatResistance;
    }

    public float getColdResistance() {
        return coldResistance;
    }

    public float getTurnSpeed() {
        return turnSpeed;
    }

    public float getBleedResistance() {
        return bleedResistance;
    }

    public float getStaminaRegen() {
        return staminaRegen;
    }

    public float getStaminaPool() {
        return staminaPool;
    }

    public Diet getDiet() {
        return diet;
    }

    public boolean isCannibalistic() {
        return cannibalistic;
    }

    public int getScentRange() {
        return scentRange;
    }

    public float getStarvationPerSec() {
        return starvationPerSec;
    }

    public float getDehydrationPerSec() {
        return dehydrationPerSec;
    }

    public int getID() {
        return ID;
    }
}
