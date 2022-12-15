package common;

import java.util.stream.IntStream;

public class Utils {
    public static IntStream getLine(int from, int to) {
        return from < to ? IntStream.rangeClosed(from, to) : IntStream.rangeClosed(to, from);
    }
}
