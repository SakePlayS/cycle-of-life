package by.sakeplays.cycle_of_life.client.screen.util;

import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.util.Util;

import java.util.List;

public class Colors {

    public static final List<ColorOption> EYE_COLORS
            = List.of(
            new ColorOption(98, 240, 255, 1f, 0.66f),
            new ColorOption(98, 135, 255, 1f, 0.66f),
            new ColorOption(255, 188, 98, 1f, 0.66f),
            new ColorOption(255, 98, 98, 1f, 0.66f),
            new ColorOption(255, 143, 181, 1f, 0.66f),
            new ColorOption(255, 135, 54, 1f, 0.66f),
            new ColorOption(185, 237, 187, 1f, 0.66f),
            new ColorOption(77, 222, 106, 1f, 0.66f),
            new ColorOption(52, 59, 192, 1f, 0.66f),
            new ColorOption(101, 255, 186, 1f, 0.66f),
            new ColorOption(147, 30, 73, 1f, 0.66f),

            new ColorOption(185, 46, 82, 1f, 0.66f)
    );

    public static List<ColorOption> LOAD_MARKINGS_COLORS(int dinoID) {

        if (dinoID == Dinosaurs.DEINONYCHUS.getID()) return DeinonychusColors.MARKINGS;
        if (dinoID == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return PachycephalosaurusColors.MARKINGS;
        if (dinoID == Dinosaurs.PTERANODON.getID()) return PteranodonColors.MARKINGS;

        return DeinonychusColors.MARKINGS;
    }

    public static List<ColorOption> LOAD_FLANK_COLORS(int dinoID) {

        if (dinoID == Dinosaurs.DEINONYCHUS.getID()) return DeinonychusColors.FLANK;
        if (dinoID == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return PachycephalosaurusColors.FLANK;
        if (dinoID == Dinosaurs.PTERANODON.getID()) return PteranodonColors.FLANK;

        return DeinonychusColors.FLANK;
    }

    public static List<ColorOption> LOAD_BODY_COLORS(int dinoID) {

        if (dinoID == Dinosaurs.DEINONYCHUS.getID()) return DeinonychusColors.BODY;
        if (dinoID == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return PachycephalosaurusColors.BODY;
        if (dinoID == Dinosaurs.PTERANODON.getID()) return PteranodonColors.BODY;

        return DeinonychusColors.BODY;
    }

    public static List<ColorOption> LOAD_BELLY_COLORS(int dinoID) {

        if (dinoID == Dinosaurs.DEINONYCHUS.getID()) return DeinonychusColors.BELLY;
        if (dinoID == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return PachycephalosaurusColors.BELLY;
        if (dinoID == Dinosaurs.PTERANODON.getID()) return PteranodonColors.BELLY;

        return DeinonychusColors.BELLY;
    }

    public static List<ColorOption> LOAD_MALE_DISPLAY_COLORS(int dinoID) {

        if (dinoID == Dinosaurs.DEINONYCHUS.getID()) return DeinonychusColors.MALE_DISPLAY;
        if (dinoID == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return PachycephalosaurusColors.MALE_DISPLAY;
        if (dinoID == Dinosaurs.PTERANODON.getID()) return PteranodonColors.MALE_DISPLAY;

        return DeinonychusColors.MALE_DISPLAY;
    }
}
