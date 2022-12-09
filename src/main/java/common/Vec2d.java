package common;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.String.format;

public class Vec2d implements Cloneable {
    private int x;
    private int y;

    public Vec2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d(Vec2d other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean sameAxis(Vec2d other) {
        return this.x == other.x || this.y == other.y;
    }

    public boolean isDiagonal() {
        return abs(this.x) == 1 && abs(this.y) == 1;
    }

    public boolean touches(Vec2d other) {
        return abs(other.x - this.x) <= 1 && abs(other.y - this.y) <= 1;
    }

    public Vec2d getDirTo(Vec2d other) {
        int xDiff = other.x - this.x;
        int yDiff = other.y - this.y;
        if (xDiff == 0 && yDiff == 0)
            return new Vec2d(0, 0);
        if (xDiff == 0) {
            return new Vec2d(0, yDiff / abs(yDiff));
        } else if (yDiff == 0){
            return new Vec2d(xDiff / abs(xDiff), 0);
        } else if (abs(xDiff) == abs(yDiff)) {
            return new Vec2d(xDiff / abs(xDiff), yDiff / abs(yDiff));
        }
        return new Vec2d(0, 0);
    }

    public boolean isOpposite(Vec2d other) {
        return this.x == -other.x && this.y == -other.y;
    }

    public void moveInDir(Dir2d dir) {
        Vec2d res = this.inDir(dir);
        this.x = res.x;
        this.y = res.y;
    }

    public void update(Vec2d other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vec2d inDir(Dir2d dir) {
        return this.add(dir.toUnit());
    }

    public Vec2d add(Vec2d other) {
        return new Vec2d(this.x + other.x, this.y + other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2d vec2d = (Vec2d) o;
        return x == vec2d.x &&
                y == vec2d.y;
    }

    @Override
    public String toString() {
        return format("(%d,%d)", x, y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
