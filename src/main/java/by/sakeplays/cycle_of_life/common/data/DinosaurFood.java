package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.entity.util.Diet;

public enum DinosaurFood {
    FOOD_NONE(0, 0, 0, 0, "minecraft:beef", Diet.CARNIVORE),

    CRIMSONWEED(0.05f, 0.007f, 0.003f, 0.03f, "cycle_of_life:crimsonweed", Diet.HERBIVORE),


    DEINONYCHUS_MEAT(0.02f, 0.01f, 0.06f, 0.03f, "minecraft:beef", Diet.CARNIVORE),
    PACHYCEPHALOSAURUS_MEAT(0.05f, 0.05f, 0.01f, 0.01f, "minecraft:beef", Diet.CARNIVORE)

    ;


    private final float proteins;
    private final float carbs;
    private final float lipids;
    private final float vitamins;
    private final String itemTexture;
    private final Diet diet;

    DinosaurFood(float proteins, float carbs, float lipids, float vitamins, String itemTexture, Diet diet) {
        this.proteins = proteins;
        this.carbs = carbs;
        this.lipids = lipids;
        this.vitamins = vitamins;
        this.itemTexture = itemTexture;
        this.diet = diet;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static DinosaurFood fromString(String s) {
        for (DinosaurFood dinosaurFood : DinosaurFood.values()) {
            if (dinosaurFood.toString().equalsIgnoreCase(s)) return dinosaurFood;
        }

        return FOOD_NONE;
    }

    public float getProteins() {
        return proteins;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getLipids() {
        return lipids;
    }

    public float getVitamins() {
        return vitamins;
    }

    public String getItemForTexture() {
        return itemTexture;
    }

    public Diet getDiet() {
        return diet;
    }
}
