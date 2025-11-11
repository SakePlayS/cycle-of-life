package by.sakeplays.cycle_of_life.entity.util;

public enum ColorableBodyParts {

    EYES("col.colorable_part.eyes"),
    MARKINGS("col.colorable_part.markings"),
    BELLY("col.colorable_part.belly"),
    BODY("col.colorable_part.body"),
    MALE_DISPLAY("col.colorable_part.male_display");

    public final String translationKey;

    ColorableBodyParts(String translationKey) {
        this.translationKey = translationKey;
    }

    public static ColorableBodyParts fromString(String string) {
        for (ColorableBodyParts part : ColorableBodyParts.values()) {
            if (part.toString().equalsIgnoreCase(string)) return part;
        }

        throw new IllegalArgumentException("Couldn't find such colorable part: " +  "\"" + string + "\"");
    }
}
