package by.sakeplays.cycle_of_life.client.screen.util;

import java.util.List;

public class Colors {

    public static final List<ColorOption> EYE_COLORS
            = List.of(
            new ColorOption(98, 240, 255, 1f),
            new ColorOption(98, 135, 255, 1f),
            new ColorOption(255, 188, 98, 1f),
            new ColorOption(255, 98, 98, 1f),
            new ColorOption(255, 143, 181, 1f),
            new ColorOption(255, 135, 54, 1f),
            new ColorOption(185, 237, 187, 1f),
            new ColorOption(77, 222, 106, 1f),
            new ColorOption(52, 59, 192, 1f),
            new ColorOption(101, 255, 186, 1f),
            new ColorOption(147, 30, 73, 1f),

            new ColorOption(185, 46, 82, 1f)
    );

    public static List<ColorOption> LOAD_MARKINGS_COLORS(int dinoID) {

        if (dinoID == 2) return DeinonychusColors.MARKINGS;

        return DeinonychusColors.MARKINGS;
    }

    public static List<ColorOption> LOAD_FLANK_COLORS(int dinoID) {

        if (dinoID == 2) return DeinonychusColors.FLANK;

        return DeinonychusColors.FLANK;
    }

    public static List<ColorOption> LOAD_BODY_COLORS(int dinoID) {

        if (dinoID == 2) return DeinonychusColors.BODY;

        return DeinonychusColors.BODY;
    }

    public static List<ColorOption> LOAD_BELLY_COLORS(int dinoID) {

        if (dinoID == 2) return DeinonychusColors.BELLY;

        return DeinonychusColors.BELLY;
    }

    public static List<ColorOption> LOAD_MALE_DISPLAY_COLORS(int dinoID) {

        if (dinoID == 2) return DeinonychusColors.MALE_DISPLAY;

        return DeinonychusColors.MALE_DISPLAY;
    }
}
