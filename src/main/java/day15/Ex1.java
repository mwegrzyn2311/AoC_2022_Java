package day15;

import common.FileParser;
import common.Vec2d;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.String.format;

public class Ex1 {
    private static final int REQUESTED_Y = 10;//2000000;
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
        List<String> input = FileParser.inputToStrList("/input15_test.txt");
        List<Pair<Integer, Integer>> xBoundsAtRequestedY = new LinkedList<>();
        Set<Integer> xPosBeaconsAtY = new HashSet<>();

        for (String line : input) {
            Matcher sensorBeaconMatcher = SENSOR_BEACON_PATTERN.matcher(line);
            if (!sensorBeaconMatcher.matches())
                throw new Exception("Unhandled input");
            Vec2d sensorPos = vecFromDay15Str(sensorBeaconMatcher.group(SENSOR_POS));
            Vec2d beaconPos = vecFromDay15Str(sensorBeaconMatcher.group(BEACON_POS));
            if (beaconPos.getY() == REQUESTED_Y)
                xPosBeaconsAtY.add(beaconPos.getX());
            Optional<Pair<Integer, Integer>> boundsOpt = boundsAtY(sensorPos, beaconPos, REQUESTED_Y);
            boundsOpt.ifPresent(xBoundsAtRequestedY::add);
        }

        xBoundsAtRequestedY.sort(Comparator.comparingInt(Pair::getLeft));

        int occupied = 0;
        int lastRight = xBoundsAtRequestedY.get(0).getLeft() - 1;
        for (Pair<Integer, Integer> bound : xBoundsAtRequestedY) {
            int left = bound.getLeft();
            int right = bound.getRight();
            if (left <= lastRight)
                left = lastRight + 1;
            if (left <= right) {
                int preRemoveSize = xPosBeaconsAtY.size();
                int finalLeft = left;
                xPosBeaconsAtY.removeIf(x -> x >= finalLeft && x <= right);
                int subtractedBeacons = preRemoveSize - xPosBeaconsAtY.size();
                occupied += right - left + 1 - subtractedBeacons;
                lastRight = right;
            }
        }

        System.out.println(occupied);
    }

    private static Optional<Pair<Integer, Integer>> boundsAtY(Vec2d sensorPos, Vec2d beaconPos, int y) {
        int dist = sensorPos.distManhattan(beaconPos);
        int yDist = abs(sensorPos.getY() - y);
        int distDiff = dist - yDist;
        if (distDiff < 0)
            return Optional.empty();
        return Optional.of(Pair.of(sensorPos.getX() - distDiff, sensorPos.getX() + distDiff));
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
