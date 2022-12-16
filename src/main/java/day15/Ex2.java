package day15;

import common.FileParser;
import common.Vec2d;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;
import static java.lang.String.format;

public class Ex2 {
    private static final int COORD_MAX = 4000000;
    private static final String X = "x";
    private static final String Y = "y";
    private static final Pattern POS_PATTERN = Pattern.compile(format("x=(?<%s>-?\\d+), y=(?<%s>-?\\d+)", X, Y));
    private static final String SENSOR_POS = "sensorPos";
    private static final String BEACON_POS = "beaconPos";
    private static final String POS_FORMAT = "x=-?\\d+, y=-?\\d+";
    private static final Pattern SENSOR_BEACON_PATTERN = Pattern.compile(
            format("Sensor at (?<%s>%s): closest beacon is at (?<%s>%s)",
                    SENSOR_POS, POS_FORMAT, BEACON_POS, POS_FORMAT));

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input15.txt");
        Map<Integer, List<Pair<Integer, Integer>>> xBoundsPerY = new HashMap<>();

        long start = System.currentTimeMillis();
        for (String line : input) {
            Matcher sensorBeaconMatcher = SENSOR_BEACON_PATTERN.matcher(line);
            if (!sensorBeaconMatcher.matches())
                throw new Exception("Unhandled input");
            Vec2d sensorPos = vecFromDay15Str(sensorBeaconMatcher.group(SENSOR_POS));
            Vec2d beaconPos = vecFromDay15Str(sensorBeaconMatcher.group(BEACON_POS));
            addBoundsPerY(xBoundsPerY, sensorPos, beaconPos);
        }

        xBoundsPerY.values().forEach(row -> row.sort(Comparator.comparingInt(Pair::getLeft)));

        for (int y = 0; y <= COORD_MAX; ++y) {
            int potentialX = beaconX(xBoundsPerY.get(y));
            if (potentialX != -1) {
                System.out.printf("x(=%d) * 4000000 + y(=%d) = %d.%n", potentialX, y, (long) potentialX * 4000000L + (long) y);
                break;
            }
        }
        long end = System.currentTimeMillis();
        System.out.printf("Time = %ds.%n", (end - start) / 1000);
    }

    private static int beaconX(List<Pair<Integer, Integer>> row) {
        int lastRight = row.get(0).getLeft() - 1;
        for (Pair<Integer, Integer> bound : row) {
            int left = bound.getLeft();
            int right = bound.getRight();
            if (left > lastRight + 1) {
                return lastRight + 1;
            }
            if (left <= lastRight)
                left = lastRight + 1;
            if (left <= right) {
                lastRight = right;
            }
        }
        return -1;
    }

    private static boolean inside(int y) {
        return y >= 0 && y <= COORD_MAX;
    }

    private static void addBoundsPerY(Map<Integer, List<Pair<Integer, Integer>>> res, Vec2d sensorPos, Vec2d beaconPos) {
        int dist = sensorPos.distManhattan(beaconPos);

        int left = Math.max(0, sensorPos.getX() - dist);
        int right = Math.min(COORD_MAX, sensorPos.getX() + dist);
        if (inside(sensorPos.getY()))
            res.computeIfAbsent(sensorPos.getY(), k -> newArrayList())
                    .add(Pair.of(left, right));
        for (int yDiff = 1; yDiff <= dist; ++yDiff) {
            int diff = dist - yDiff;
            int yLower = sensorPos.getY() - yDiff;
            int yHigher = sensorPos.getY() + yDiff;
            left = Math.max(0, sensorPos.getX() - diff);
            right = Math.min(COORD_MAX, sensorPos.getX() + diff);
            if (left > right) {
                System.out.println("Hmmm...");
                return;
            }
            if (yLower >= 0)
                res.computeIfAbsent(yLower, k -> newArrayList()).add(Pair.of(left, right));
            if (yHigher <= COORD_MAX)
                res.computeIfAbsent(yHigher, k -> newArrayList()).add(Pair.of(left, right));
        }
    }

    private static Vec2d vecFromDay15Str(String vecStr) throws Exception {
        Matcher vecMatcher = POS_PATTERN.matcher(vecStr);
        if (!vecMatcher.matches())
            throw new Exception(format("Unhandled input: %s", vecStr));
        int x = Integer.parseInt(vecMatcher.group(X));
        int y = Integer.parseInt(vecMatcher.group(Y));
        return new Vec2d(x, y);
    }
}
