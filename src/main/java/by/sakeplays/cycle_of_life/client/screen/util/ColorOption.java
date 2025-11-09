package by.sakeplays.cycle_of_life.client.screen.util;

public record ColorOption(int r, int g, int b, float a, float minBrigthness) {
    public int toInt() {
        int ri = (int) (r);
        int gi = (int) (g);
        int bi = (int) (b);
        int ai = (int) (a * 255.0f);
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }

    public static ColorOption fromInt(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        float af = a / 255f;
        return new ColorOption(r, g, b, af, 1f);
    }
}
