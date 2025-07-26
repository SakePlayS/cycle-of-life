package by.sakeplays.cycle_of_life.entity.util;

public enum Dinosaurs {

    NONE(0f,0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 0f, 1f, 0, 0f,
            0f, 0, Diet.HERBIVORE, false, 0f,0f ,0),

    PACHYCEPHALOSAURUS(0.12f,0.55f, 5f, 425f, 3f, 4f,
            12f, 0.32f, 0.005f, 1.5f, 1750f, 350, 0.035f,
            0.00065f, 0.00045f, Diet.HERBIVORE, false, 0.49f,0.00045f ,1),

    DEINONYCHUS(0.17f,0.67f,5f,75f, 5f, 12f,
            17f, 0.28f, 0.007f, 0.8f, 3800f, 200, 0.05f,
            0.00031f, 0.00037f, Diet.CARNIVORE, false, 0.56f, 0.0005f,2),

    QUETZALCOATLUS(0.07f,0.25f, 0.08f, 225f, 3f, 4f,
            8f, 0.15f, 0.002f, 0.8f, 1150f, 650, 0.01f,
            0.00045f, 0.0008f, Diet.CARNIVORE, false, 0.65f,0.00027f ,3),

    UTAHRAPTOR(0.18f,0.61f, 0.08f, 225f, 3f, 4f,
            8f, 0.15f, 0.002f, 0.4f, 1150f, 650, 0.01f,
            0.00045f, 0.0008f, Diet.CARNIVORE, false, 0.45f,0.00039f ,4);



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
    private final float swimSpeed;
    private final float walkSpeed;
    private final float sprintSpeed;
    private final float acceleration;
    private final float growthPerMin;
    private final float startWeight;
    private final float healthRegen;


    Dinosaurs(float walkSpeed, float sprintSpeed, float growthPerMin, float weight, float heatResistance, float coldResistance, float turnSpeed,
              float swimSpeed, float bleedResistance, float staminaRegen, float staminaPool, int scentRange, float acceleration,
              float starvationPerSec, float dehydrationPerSec, Diet diet, boolean cannibalistic, float startWeight,
              float healthRegen, int ID) {
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
        this.swimSpeed = swimSpeed;
        this.walkSpeed = walkSpeed;
        this.sprintSpeed = sprintSpeed;
        this.acceleration = acceleration;
        this.growthPerMin = growthPerMin;
        this.startWeight = startWeight;
        this.healthRegen = healthRegen;
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

    public static Dinosaurs getById (int ID) {
        if (ID == 1) return PACHYCEPHALOSAURUS;
        if (ID == 2) return DEINONYCHUS;
        if (ID == 3) return QUETZALCOATLUS;
        if (ID == 4) return UTAHRAPTOR;

        return NONE;

    }

    public float getSwimSpeed() {
        return swimSpeed;
    }

    public float getSprintSpeed() {
        return sprintSpeed;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getGrowthPerMin() {
        return growthPerMin;
    }

    public float getStartWeight() {
        return startWeight;
    }

    public float getHealthRegen() {
        return healthRegen;
    }
}
