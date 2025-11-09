package by.sakeplays.cycle_of_life.entity.util;

import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorOptionArray {

    private Map<ColorableBodyParts, List<ColorOption>> colorOptions = new HashMap<>();

    private List<ColorOption> queuedColorOptions = new ArrayList<>();

    public ColorOptionArray addColorOption(int r, int g, int b, float minBrightness) {
        queuedColorOptions.add(new ColorOption(r, g, b, 1, minBrightness));
        return this;
    }

    public ColorOptionArray buildCategory(ColorableBodyParts part) {
        colorOptions.put(part, new ArrayList<>(queuedColorOptions));
        queuedColorOptions.clear();
        return this;
    }

    public Map<ColorableBodyParts, List<ColorOption>> getColorOptions() {
        return new HashMap<>(colorOptions);
    }

    public static final ColorOptionArray DEINONYCHUS_COLORS = new ColorOptionArray()
            .addColorOption(98, 240, 255, 0.66f)
            .addColorOption(98, 135, 255, 0.66f)
            .buildCategory(ColorableBodyParts.EYES)

            .addColorOption(43, 79, 53, 0.85f)
            .addColorOption(94, 56, 56, 0.85f)
            .addColorOption(72, 73, 95, 0.85f)
            .buildCategory(ColorableBodyParts.BODY)

            .addColorOption(167, 79, 122, 0.66f)
            .addColorOption(110, 119, 172, 0.66f)
            .addColorOption(91, 143, 116, 0.66f)
            .buildCategory(ColorableBodyParts.MARKINGS)

            .addColorOption(255, 126, 126, 0.66f)
            .addColorOption(255, 219, 126, 0.66f)
            .addColorOption(124, 182, 206, 0.66f)
            .buildCategory(ColorableBodyParts.MALE_DISPLAY)

            .addColorOption(225, 250, 255, 0.8f)
            .addColorOption(241, 255, 220, 0.8f)
            .addColorOption(41, 96, 64, 0.8f)
            .addColorOption(117, 69, 68, 0.8f)
            .addColorOption(87, 87, 113, 0.8f)
            .buildCategory(ColorableBodyParts.BELLY);

}
