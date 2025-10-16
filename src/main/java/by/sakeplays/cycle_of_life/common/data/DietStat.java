package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public enum DietStat {
    HEALTH_REGEN,
    BLOOD_REGEN,
    STAMINA_REGEN,
    STAMINA_POOL,
    GROWTH_SPEED,
    IMMUNE_SYS_STRENGTH,
    DAMAGE,
    TEMPERATURE_RESISTANCE,
    BLEED_RESISTANCE,
    SPEED;

    private static float multiplier(float value, float min, float max, Player player) {
        if (value < 0.4f) {
            float delta = (0.4f - value) * 2.5f;
            return Mth.lerp(delta * delta, 1f, Mth.lerp(dietQuality(player), min, 1f));
        } else if (value > 0.6f) {
            float delta = (value - 0.6f) * 2.5f + 0.05f;
            return Mth.lerp(Math.min(1f, delta * delta * delta), 1f, Mth.lerp(Math.min(1f, dietQuality(player) + 0.05f), 1f, max));
        }

        return 1f;
    }

    public static float dietQuality(Player player) {
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float proteins = data.getProteins();
        float carbs = data.getCarbs();
        float lipids = data.getLipids();
        float vitamins = data.getVitamins();

        return (proteins + carbs + lipids + vitamins)/4;
    }

    public static float calculate(Player player, DietStat stat) {
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        return switch (stat) {
            case STAMINA_REGEN -> multiplier(data.getCarbs(), 0.5f, 1.25f, player);
            case STAMINA_POOL -> multiplier(data.getCarbs(), 0.65f, 1.1f, player);
            case SPEED -> multiplier(data.getCarbs(), 0.85f, 1.05f, player);
            case DAMAGE -> multiplier(data.getProteins(), 0.75f, 1.1f, player);
            case GROWTH_SPEED -> multiplier(data.getProteins(), 0.1f, 1.75f, player);
            case BLEED_RESISTANCE -> multiplier(data.getLipids(), 0.5f, 1.2f, player);
            case BLOOD_REGEN -> multiplier(data.getLipids(), 0.2f, 1.35f, player);
            case IMMUNE_SYS_STRENGTH -> multiplier(data.getVitamins(), 0.1f, 2f, player);
            case HEALTH_REGEN -> multiplier(data.getVitamins(), 0.2f, 1.35f, player);
            case TEMPERATURE_RESISTANCE -> multiplier(data.getVitamins(), 0.5f, 1.2f, player);
        };
    }
}
