package by.sakeplays.cycle_of_life.entity.util;

import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import it.unimi.dsi.fastutil.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorOptionArray {

    private Map<ColorableBodyParts, List<Pair<ColorOption, ColorOption>>> colorOptions = new HashMap<>();

    private List<Pair<ColorOption, ColorOption>> queuedColorOptions = new ArrayList<>();

    public ColorOptionArray addColorOption(ColorOption primary, ColorOption secondary) {
        queuedColorOptions.add(Pair.of(primary, secondary));
        return this;
    }

    public ColorOptionArray buildCategory(ColorableBodyParts part) {
        colorOptions.put(part, new ArrayList<>(queuedColorOptions));
        queuedColorOptions.clear();
        return this;
    }

    public Map<ColorableBodyParts, List<Pair<ColorOption, ColorOption>>> getColorOptions() {
        return new HashMap<>(colorOptions);
    }

    public static final ColorOptionArray DEINONYCHUS_COLORS = new ColorOptionArray()
            .addColorOption(new ColorOption(140, 255, 247), new ColorOption(51, 100, 143))
            .addColorOption(new ColorOption(128, 255, 161), new ColorOption(52, 132, 147))
            .addColorOption(new ColorOption(255, 247, 108), new ColorOption(185, 64, 0))
            .addColorOption(new ColorOption(192, 255, 108), new ColorOption(18, 109, 46))
            .addColorOption(new ColorOption(255, 142, 93), new ColorOption(130, 25, 80))
            .addColorOption(new ColorOption(237, 255, 255), new ColorOption(125, 142, 199))
            .buildCategory(ColorableBodyParts.EYES)

            .addColorOption(new ColorOption(71, 85, 106), new ColorOption(23, 17, 40))
            .addColorOption(new ColorOption(85, 33, 33), new ColorOption(4, 1, 1))
            .addColorOption(new ColorOption(178, 189, 189), new ColorOption(49, 61, 89))
            .addColorOption(new ColorOption(73, 103, 77), new ColorOption(12, 36, 36))
            .addColorOption(new ColorOption(98, 117, 114), new ColorOption(20, 38, 52))
            .addColorOption(new ColorOption(147, 140, 126), new ColorOption(38, 23, 17))
            .addColorOption(new ColorOption(230, 210, 200), new ColorOption(39, 10, 10))

            .buildCategory(ColorableBodyParts.BODY)

            .addColorOption(new ColorOption(202, 96, 96), new ColorOption(159, 61, 97))
            .addColorOption(new ColorOption(107, 168, 179), new ColorOption(67, 104, 130))
            .addColorOption(new ColorOption(93, 173, 119), new ColorOption(57, 118, 109))
            .addColorOption(new ColorOption(148, 184, 189), new ColorOption(98, 115, 123))
            .buildCategory(ColorableBodyParts.MARKINGS)

            .addColorOption(new ColorOption(231, 106, 106), new ColorOption(168, 62, 114))
            .addColorOption(new ColorOption(101, 175, 195), new ColorOption(66, 112, 157))
            .buildCategory(ColorableBodyParts.MALE_DISPLAY)

            .addColorOption(new ColorOption(191, 221, 223), new ColorOption(117, 120, 153))
            .addColorOption(new ColorOption(218, 199, 157), new ColorOption(130, 96, 75))
            .buildCategory(ColorableBodyParts.BELLY);

}
