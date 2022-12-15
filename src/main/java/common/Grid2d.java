package common;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.Utils.getLine;
import static java.lang.String.format;

public class Grid2d<T> {
    protected final Map<Vec2d, T> grid = new HashMap();

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

    public Map<Vec2d, T> getGrid() {
        return this.grid;
    }

    public boolean hasEleAt(Vec2d pos) {
        return grid.containsKey(pos);
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

    public List<List<Vec2d>> calculateBounds() {
        int left = grid.keySet().stream().mapToInt(Vec2d::getX).min().orElse(0);
        int top = grid.keySet().stream().mapToInt(Vec2d::getY).min().orElse(0);
        int right = grid.keySet().stream().mapToInt(Vec2d::getX).max().orElse(1);
        int bot = grid.keySet().stream().mapToInt(Vec2d::getY).max().orElse(1);
        return getLine(top, bot)
                .mapToObj(y -> getLine(left, right)
                        .mapToObj(x -> new Vec2d(x, y))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }



    @Override
    public String toString() {
        return calculateBounds().stream()
                .map(row -> row.stream()
                        .map(pos -> hasEleAt(pos) ? grid.get(pos).toString() : ".")
                        .collect(Collectors.joining("")))
                .collect(Collectors.joining("\n"));
    }
}
