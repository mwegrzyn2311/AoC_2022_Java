package common;

import static java.lang.String.format;

public enum Dir2d {
    UP(new Vec2d(0, 1)),
    DOWN(new Vec2d(0, -1)),
    LEFT(new Vec2d(-1, 0)),
    RIGHT(new Vec2d(1, 0));

    private final Vec2d unitVector;

    Dir2d(Vec2d unitVector) {
        this.unitVector = unitVector;
    }

    public Vec2d toUnit() {
        return this.unitVector;
    }

    public boolean isOpposite(Dir2d other) {
        return switch (this) {
            case UP -> other == DOWN;
            case DOWN -> other == UP;
            case LEFT -> other == RIGHT;
            case RIGHT -> other == LEFT;
        };
    }

    public static Dir2d fromChar(char c) throws Exception {
        return switch (Character.toLowerCase(c)) {
            case 'u' -> UP;
            case 'd' -> DOWN;
            case 'l' -> LEFT;
            case 'r' -> RIGHT;
            default -> throw new Exception(format("Unhandled input %c in Dir2d#fromChar", c));
        };
    }
}
