package by.sakeplays.cycle_of_life.client.screen.util;

public record ColorOption(int r, int g, int b) {
    public int toInt() {
        int ri = (r);
        int gi = (g);
        int bi = (b);
        int ai = 255;
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }

    public static ColorOption fromInt(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        return new ColorOption(r, g, b);
    }
}
