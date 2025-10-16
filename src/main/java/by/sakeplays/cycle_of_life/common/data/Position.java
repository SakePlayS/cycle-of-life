package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;

public class Position {

    private double x;
    private double y;
    private double z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public String toShortString() {
        DecimalFormat format = new DecimalFormat("#.##");

        return "(" + format.format(x) + ", " + format.format(y) + ", " + format.format(z) + ")";
    }
}
