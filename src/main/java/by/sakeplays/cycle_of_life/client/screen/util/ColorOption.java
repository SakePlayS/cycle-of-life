package by.sakeplays.cycle_of_life.client.screen.util;

public record ColorOption(int r, int g, int b, float a) {
    public int toInt() {
        int ri = (int) (r);
        int gi = (int) (g);
        int bi = (int) (b);
        int ai = (int) (a * 255.0f);
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }
}
