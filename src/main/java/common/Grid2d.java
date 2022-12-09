package common;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class Grid2d<T> {
    private final Map<Vec2d, T> grid = new HashMap();

    public Grid2d() {}

    /**
     * Note that by initing with defaultObj all fields contains the same reference so T should be immutable
     */
    public Grid2d(int width, int height, T defaultObj) {
        IntStream.range(0, width)
                .forEach(x -> IntStream.range(0, height)
                        .mapToObj(y -> new Vec2d(x, y))
                        .forEach(pos -> grid.put(pos, defaultObj)));
    }

    public void add(Vec2d pos, T ele) {
        this.grid.put(pos, ele);
    }

    public int numberOfElements() {
        return this.grid.size();
    }

    public Collection<T> allElements() {
        return this.grid.values();
    }

    public Optional<T> eleAt(Vec2d pos) {
        if (grid.containsKey(pos))
            return Optional.of(grid.get(pos));
        else
            return Optional.empty();
    }

    public void putOrModify(Vec2d pos, Function<Optional<T>, T> putOrModifyOperation) {
        grid.put(pos, putOrModifyOperation.apply(this.eleAt(pos)));
    }

    public void modify(Vec2d pos, Function<T, T> operation) {
        if (grid.containsKey(pos))
            grid.put(pos, operation.apply(grid.get(pos)));
    }
}
