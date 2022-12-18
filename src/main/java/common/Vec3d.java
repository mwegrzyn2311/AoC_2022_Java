package common;

import static java.lang.Math.abs;
import static java.lang.String.format;

public class Vec3d {
    private int x;
    private int y;
    private int z;

    public Vec3d(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isAdjacent(Vec3d other) {
        return (this.x == other.x && this.y == other.y && abs(this.z - other.z) == 1) ||
                (this.x == other.x && abs(this.y - other.y) == 1 && this.z == other.z) ||
                (abs(this.x - other.x) == 1 && this.y == other.y && this.z == other.z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return format("(%d,%d, %d)", x, y, z);
    }
}
